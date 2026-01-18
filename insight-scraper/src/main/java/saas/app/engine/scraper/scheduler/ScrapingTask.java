package saas.app.engine.scraper.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import saas.app.core.domain.ProductLink;
import saas.app.core.dto.ProductUpdateEvent;
import saas.app.core.repository.ProductLinkRepository;
import saas.app.core.config.RabbitConfig;
import saas.app.engine.scraper.dto.ScraperData;
import saas.app.engine.scraper.service.ScraperService;


import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class ScrapingTask { //pasa de ser una especie de "vigilante" a un recolector de datos
    private final ProductLinkRepository linkRepository;
    private final ScraperService scraperService;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 60000)
    public void runScrapingCyle(){
        log.info("Iniciando ciclo de scraping...");
        try {
            List<ProductLink> links = linkRepository.findAll();
            if (links.isEmpty()) {
                log.info("No hay links para procesar.");
            }
            for (ProductLink link : links) {
                processLink(link);
            }
        } catch (Exception e) {
            log.error("Error en el ciclo de scraping: {}", e.getMessage());
        }

    }


    private void processLink(ProductLink link) {
        try {
            log.info("Analizando: {} enla tienda {} ", link.getProduct().getName(), link.getStoreName());

            ScraperData scraperData = scraperService.getLastestData(link);

            if (scraperData != null){
                ProductUpdateEvent event = ProductUpdateEvent.builder()
                        .productId(link.getProduct().getId())
                        .linkId(link.getId())
                        .storeName(link.getStoreName())
                        .currentPrice(scraperData.getPrice())
                        .installments(scraperData.getInstallments())
                        .hasFreeShipping(scraperData.getHasFreeShipping())
                        .isAvailable(scraperData.getIsAvailable())
                        .lastChecked(LocalDateTime.now())
                        .imageUrl(scraperData.getImageUrl())
                        .sizes(scraperData.getSizes())
                        .build();

                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);

                log.info("Datos enviados a la cola para: {} - ${} ", link.getStoreName(), scraperData.getPrice());
            }



        } catch (Exception e){
            log.error("Error procesando el link {}: {} ", link.getUrl(), e.getMessage());
        }
    }
}




