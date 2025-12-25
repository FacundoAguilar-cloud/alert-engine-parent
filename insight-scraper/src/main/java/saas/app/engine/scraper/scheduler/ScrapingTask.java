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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScrapingTask {
    private final MonitoredSiteRepository siteRepository;
    private final SiteSnapshotRepository snapshotRepository;
    private final ScraperService scraperService;

    @Scheduled(fixedRate = 60000)
    public void runScrapingCycle(){
    log.info("Iniciando ciclo de scraping");

        List <MonitoredSite> sites = siteRepository.findByActiveTrue();

        for (MonitoredSite site : sites){
        processSite(site);
    }
}

private void processSite(MonitoredSite site){
       try {
           log.info("Analizando: {}", site.getName());

           String currentValue = scraperService.getElementText(site.getUrl(), site.getCssSelector());

           SiteSnapshot snapshot = SiteSnapshot.builder()
                   .monitoredSite(site)
                   .capturedValue(currentValue)
                   .build();

           snapshotRepository.save(snapshot);
           log.info("Valor guardado {}", currentValue);

           site.setLastCheckedAt(java.time.LocalDateTime.now());
           siteRepository.save(site);
       } catch (Exception e){
           log.error("Error", site.getName(), e.getMessage());
    }

}
}

