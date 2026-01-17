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
    public void receiveMessage(ProductUpdateEvent event){
        log.info(" Procesando actualización para el producto : {} en la tienda: {}.", event.getProductId(), event.getStoreName());

        //buscamos link en la DB
        ProductLink link = linkRepository
                .findById(event.getLinkId()).orElseThrow(() -> new RuntimeException("Link no encontrado"));
        boolean isDeal = false;
        if (link.getCurrentPrice() !=null && event.getCurrentPrice().compareTo(link.getCurrentPrice()) < 0){

            isDeal = true;
            log.warn("¡Oferta detectada! El precio bajó de {} a {}", link.getCurrentPrice(), event.getCurrentPrice());

        }

        String sizesText = "";
        if (event.getSizes() != null){
            String sizesList = event.getSizes().stream()
                    .filter(SizeStockDTO::getAvailable)
                    .map(SizeStockDTO::getSize)
                    .collect(Collectors.joining(", "));
        }

        //actualizamos el estado actual de las cosas.
        link.setImageUrl(event.getImageUrl());
        link.setAvailableSizes(sizesText);
        link.setCurrentPrice(event.getCurrentPrice());
        link.setMaxInstallments(event.getInstallments());
        link.setHasFreeShipping(event.getHasFreeShipping());
        link.setLastChecked(event.getLastChecked());
        link.setIsAvailable(event.getIsAvailable());

        linkRepository.save(link);

        PriceHistory history = PriceHistory.builder()
                .productLink(link)
                .price(event.getCurrentPrice())
                .detectedAt(event.getLastChecked())
                .build();
            historyRepository.save(history);

            if (isDeal){
                String message = String.format("Nuevo precio bajo: %s\nTalles disponibles: %s", event.getCurrentPrice()
                        , sizesText.isEmpty() ? "Sin stock" : sizesText);
                telegramService
                        .sendMeTelegramAlert(link.getStoreName(),
                                "Precio anterior:" + link.getCurrentPrice()
                                ,"NUEVO PRECIO BAJO:" + event.getCurrentPrice());
            }

    }
}
