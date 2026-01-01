package saas.app.engine.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import saas.app.core.domain.SiteChangeLog;
import saas.app.core.dto.PriceUpdateEvent;
import saas.app.core.repository.SiteChangeLogRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class SiteChangeConsumer {
    private  final SiteChangeLogRepository repository;

    @RabbitListener(queues = "q.site.alerts")
    public void receiveMessage(PriceUpdateEvent event){
        log.info("Mensaje recibido en la API.");
        log.info("Evento de cambio recibido '{}' ", event.getSiteName());

        SiteChangeLog logEntry = SiteChangeLog.builder()
                .siteId(event.getSiteId())
                .siteName(event.getSiteName())
                .oldValue(event.getOldValue())
                .newValue(event.getNewValue())
                .detectedAt(event.getTimestamp())
                .build();

            repository.save(logEntry);
            log.info("Historial de cambio persistido en la BD.");
    }
}
