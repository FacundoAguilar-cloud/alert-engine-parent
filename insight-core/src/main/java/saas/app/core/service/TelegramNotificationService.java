package saas.app.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
public class TelegramNotificationService {

    private static final String BOT_TOKEN = "7805759839:AAGXyWVqr7kdahUxKUK8wSslIwrAORdjxHw";
    private static final String CHAT_ID = "5581094202";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMeTelegramAlert(String siteName, String oldValue, String newValue){
        try {
            String message =String.format(
                    "*ALERTA DE CAMBIO DETECTADA*\\n\\n" +
                    "*Sitio:* %s\n" +
                    "*Antes:* %s\n" +
                    "*Ahora:* %s\n" +
                    "_Verificado el: %s_",
                    siteName, oldValue, newValue, java.time.LocalDateTime.now()
            );

                    String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

                    //URL oficial de la API de Telegram
                    String url = String.format(
                            "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=Markdown",
                            BOT_TOKEN, CHAT_ID, encodedMessage
                    );

                    restTemplate.getForObject(url, String.class);
                    log.info("Notificación de Telegram enviada para: {}", siteName);

        }catch (Exception e){
            log.error("Error enviado a Telegram{}", e.getMessage());
        }
    }
}
