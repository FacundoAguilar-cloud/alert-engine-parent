package saas.app.engine.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cglib.core.Local;
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
import java.time.LocalDateTime;
import java.util.Optional;
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

        BigDecimal newPrice = event.getCurrentPrice();
        log.info(" Procesando actualización para el producto : {} en la tienda: {}.", event.getProductId(), event.getStoreName());

        //buscamos link en la DB
        ProductLink link = linkRepository
                .findById(event.getLinkId()).orElseThrow(() -> new RuntimeException("Link no encontrado"));

        //COMPARACIÓN INTELIGENTE DE PRECIO

        Optional <PriceHistory> lastHistory = historyRepository.findFirstByProductLinkIdOrderByDetectedAtDesc(link.getId());


        boolean realPriceChanged = (lastHistory.isEmpty() || lastHistory.get().getPrice().compareTo(newPrice) != 0);

        if (realPriceChanged) {
            log.info("Cambio de precio detectado para {}:  -> {}", link.getStoreName(), newPrice);

            log.info("HISTORIAL GUARDADO EXITOSAMENTE");

            PriceHistory history = PriceHistory.builder()
                    .productLink(link)
                    .price(newPrice)
                    .detectedAt(LocalDateTime.now())
                    .build();
            historyRepository.save(history);


            if (lastHistory.isPresent() & newPrice.compareTo(lastHistory.get().getPrice()) < 0) {
                telegramService.sendMeTelegramAlert(link
                        .getStoreName(), "Precio anterior" + lastHistory.get().getPrice(), "OFERTA! Nuevo precio" + newPrice);
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
        link.setLastChecked(LocalDateTime.now());
        link.setIsAvailable(event.getIsAvailable());

        linkRepository.save(link);



    }
}
