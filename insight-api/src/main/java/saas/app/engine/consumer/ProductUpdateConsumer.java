package saas.app.engine.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import saas.app.core.config.RabbitConfig;
import saas.app.core.domain.ProductLink;
import saas.app.core.domain.PriceHistory;
import saas.app.core.dto.ProductUpdateEvent;
import saas.app.core.dto.SizeStockDTO;
import saas.app.core.repository.PriceHistoryRepository;
import saas.app.core.repository.ProductLinkRepository;
import saas.app.core.service.TelegramNotificationService;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductUpdateConsumer {
    private final PriceHistoryRepository historyRepository;
    private final ProductLinkRepository linkRepository;
    private final TelegramNotificationService telegramService;


    @RabbitListener(queues = RabbitConfig.QUEUE_ALERTS, containerFactory = "rabbitListenerContainerFactory")
    @Transactional
    public void receiveMessage(ProductUpdateEvent event) {
        log.info(" Procesando actualización para el producto : {} en la tienda: {}.", event.getProductId(), event.getStoreName());

        //buscamos link en la DB
        ProductLink link = linkRepository
                .findById(event.getLinkId()).orElseThrow(() -> new RuntimeException("Link no encontrado"));

        //COMPARACIÓN INTELIGENTE DE PRECIO
        BigDecimal oldPrice = link.getCurrentPrice();
        BigDecimal newPrice = event.getCurrentPrice();

        boolean priceChanged = (oldPrice == null && newPrice != null) || (oldPrice != null && newPrice != null &&
                oldPrice.compareTo(newPrice) != 0);

        if (priceChanged) {
            log.info("Cambio de precio detectado para {}: {} -> {}", link.getStoreName(), oldPrice, newPrice);

            PriceHistory history = PriceHistory.builder()
                    .productLink(link)
                    .price(newPrice)
                    .detectedAt(event.getLastChecked())
                    .build();
            historyRepository.save(history);


            if (oldPrice != null && newPrice.compareTo(oldPrice) < 0) {
                telegramService.sendMeTelegramAlert(link
                        .getStoreName(), "Precio anterior" + oldPrice, "OFERTA! Nuevo precio" + newPrice);
            }
        }


        String sizesText = "";
        if (event.getSizes() != null && !event.getSizes().isEmpty()) {
            sizesText = event.getSizes().stream()
                    .filter(SizeStockDTO::getAvailable)
                    .map(SizeStockDTO::getSize)
                    .collect(Collectors.joining(", "));
        }

        //actualizamos el estado actual de las cosas.
        link.setImageUrl(event.getImageUrl());
        link.setAvailableSizes(sizesText);
        link.setCurrentPrice(newPrice);
        link.setMaxInstallments(event.getInstallments());
        link.setHasFreeShipping(event.getHasFreeShipping());
        link.setLastChecked(event.getLastChecked());
        link.setIsAvailable(event.getIsAvailable());

        linkRepository.save(link);





    }
}
