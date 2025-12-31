package saas.app.engine.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import saas.app.core.dto.SiteChangeEvent;

@Component
@Slf4j
public class SiteChangeConsumer {

    @RabbitListener(queues = "q.site.alerts")
    public void receiveMessage(SiteChangeEvent event){
        log.info("Mensaje recibido en la API.");
        log.info("El sitio '{}' cambio de '{}' a '{}'", event.getSiteName(), event.getOldValue(), event.getNewValue());
    }
}
