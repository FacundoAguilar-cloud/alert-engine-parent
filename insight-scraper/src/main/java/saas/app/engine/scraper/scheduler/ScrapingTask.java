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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class ScrapingTask { //pasa de ser una especie de "vigilante" a un recolector de datos
    private final ProductLinkRepository linkRepository;
    private final ScraperService scraperService;
    private final RabbitTemplate rabbitTemplate;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    //vamos a aplicar hilos para acelerar el proceso de scraping



    @Scheduled(fixedRate = 60000)
    public void runScrapingCyle(){
        log.info("Iniciando ciclo de scraping PARALELO");

            List<ProductLink> links = linkRepository.findAll();
            if (links.isEmpty()) {
                log.info("No hay links para procesar.");
            }
            // basicamente esto sería una lista de tareas futuras
            List <CompletableFuture<Void>> futures = links
                    .stream()
                    .map(link -> CompletableFuture.runAsync(() -> processLink(link), executor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenRun( () -> log.info("Ciclo de scraping finalizado con éxito."))
                    .join(); //esto sirve para que el shceduled espere a que todos terminen
        }

    private void processLink(ProductLink link) {
        try {
            log.info("[Hilo: {}] Analizando: {} en {}", Thread.currentThread().getName(), link.getProduct().getName(), link.getStoreName());

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
            log.error("Error en hilo al procesar {}: {} ", link.getStoreName(), e.getMessage());
        }
    }

    }








