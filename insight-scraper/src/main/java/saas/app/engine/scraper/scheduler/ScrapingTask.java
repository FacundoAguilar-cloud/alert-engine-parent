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
import java.util.Optional;

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
            //valor actual de internet
           String currentValue = scraperService.getElementText(site.getUrl(), site.getCssSelector());
            //buscamos el ultimo snapshot
           Optional <SiteSnapshot> lastSnapshot = snapshotRepository.findFirstByMonitoredSiteIdOrderBySnapshotTimeDesc(site.getId());

           //establecemos una logica de comparación
           if (lastSnapshot.isPresent()){
               String previousValue = lastSnapshot.get().getCapturedValue();

           if (!currentValue.equals(previousValue)){
               log.warn("ALERTA DE CAMBIO DETECTADA");
               log.warn("Sitio {}", site.getName());
               log.warn("Antes {}",previousValue);
               log.warn("Despues {}", currentValue);
           }
           else {
               log.info("Sin cambios detectados, el valor sigue siendo el {}", currentValue);
           }
           } else {
               log.info("Primer escaneo para este sitio, Guardando valor inicial ");
           }

               SiteSnapshot snapshot = SiteSnapshot.builder()
                   .monitoredSite(site)
                   .capturedValue(currentValue)
                   .snapshotTime(java.time.LocalDateTime.now())
                   .build();

           snapshotRepository.save(snapshot);
           log.info("Valor guardado {}", currentValue);

       } catch (Exception e){
           log.error("Error a la hora de procesar el sitio {}: {} ", site.getName(), e.getMessage());

    }

}
}

