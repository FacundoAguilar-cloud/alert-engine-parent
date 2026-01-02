package saas.app.engine.scraper.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import saas.app.core.domain.Product;
import saas.app.core.domain.SiteSnapshot;
import saas.app.core.dto.ProductUpdateEvent;
import saas.app.core.repository.SiteSnapshotRepository;
import saas.app.engine.scraper.config.RabbitConfig;
import saas.app.engine.scraper.service.ScraperService;
import saas.app.core.service.TelegramNotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScrapingTask {
    private final MonitoredSiteRepository siteRepository;
    private final SiteSnapshotRepository snapshotRepository;
    private final ScraperService scraperService;
    private final TelegramNotificationService telegramService;

    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 60000)
    public void runScrapingCyle(){
        log.info("Iniciando ciclo de scraping...");
        try {
            List<Product> sites = siteRepository.findByActiveTrue();
            if (sites.isEmpty()) {
                log.info("No hay sitios activos para monitorear.");
            }
            for (Product site : sites) {
                processSite(site);
            }
        } catch (Exception e) {
            log.error("Error en el ciclo de scraping: {}", e.getMessage());
        }

    }


    private void processSite(Product site) {
        try {
            log.info("Analizando: {}", site.getName());
            String currentValue = scraperService.getElementText(site.getUrl(), site.getCssSelector());

            Optional<SiteSnapshot> lastSnapshot = snapshotRepository.findFirstByMonitoredSiteIdOrderBySnapshotTimeDesc(site.getId());

            if (lastSnapshot.isPresent()) {
                String previousValue = lastSnapshot.get().getCapturedValue();

                if (!currentValue.equals(previousValue)) {
                    log.warn("ALERTA DE CAMBIO DETECTADA");
                 ProductUpdateEvent event = ProductUpdateEvent.builder()
                         .siteId(site.getId())
                         .siteName(site.getName())
                         .oldValue(previousValue)
                         .newValue(currentValue)
                         .timestamp(java.time.LocalDateTime.now())
                         .build();

                 rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE,
                         RabbitConfig.ROUTING_KEY, event);
                 log.info("Evento enviado a RabbitMQ correctamente.");

                 telegramService.sendMeTelegramAlert(site.getName(), previousValue, currentValue);

                 site.setLastCheckedAt(LocalDateTime.now());
                 siteRepository.save(site);
                }
            }

            SiteSnapshot snapshot = SiteSnapshot.builder()
                    .product(site)
                    .capturedValue(currentValue)
                    .snapshotTime(java.time.LocalDateTime.now())
                    .build();

            snapshotRepository.save(snapshot);
            log.info("Valor guardado {}", currentValue);

        } catch (Exception e) { // <-- ESTO FALTABA
            log.error("Error procesando el sitio {}: {}", site.getName(), e.getMessage());
        }
    }
}




