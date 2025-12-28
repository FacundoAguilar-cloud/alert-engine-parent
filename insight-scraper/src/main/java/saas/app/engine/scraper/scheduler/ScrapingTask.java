package saas.app.engine.scraper.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import saas.app.core.domain.MonitoredSite;
import saas.app.core.domain.SiteSnapshot;
import saas.app.core.repository.MonitoredSiteRepository;
import saas.app.core.repository.SiteSnapshotRepository;
import saas.app.engine.scraper.service.ScraperService;
import saas.app.engine.scraper.service.TelegramNotificationService;

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

    @Scheduled(fixedRate = 60000)
    public void runScrapingCyle(){
        log.info("Iniciando ciclo de scraping...");
        try {
            List<MonitoredSite> sites = siteRepository.findByActiveTrue();
            if (sites.isEmpty()) {
                log.info("No hay sitios activos para monitorear.");
            }
            for (MonitoredSite site : sites) {
                processSite(site);
            }
        } catch (Exception e) {
            log.error("Error en el ciclo de scraping: {}", e.getMessage());
        }

    }


    private void processSite(MonitoredSite site) {
        try {
            log.info("Analizando: {}", site.getName());
            String currentValue = scraperService.getElementText(site.getUrl(), site.getCssSelector());

            Optional<SiteSnapshot> lastSnapshot = snapshotRepository.findFirstByMonitoredSiteIdOrderBySnapshotTimeDesc(site.getId());

            if (lastSnapshot.isPresent()) {
                String previousValue = lastSnapshot.get().getCapturedValue();

                if (!currentValue.equals(previousValue)) {
                    log.warn("ALERTA DE CAMBIO DETECTADA");
                    // Asegúrate de que el nombre coincida con tu Service
                    telegramService.sendMeTelegramAlert(site.getName(), previousValue, currentValue);
                    site.setLastCheckedAt(java.time.LocalDateTime.now());
                    siteRepository.save(site);
                }
            }

            SiteSnapshot snapshot = SiteSnapshot.builder()
                    .monitoredSite(site)
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




