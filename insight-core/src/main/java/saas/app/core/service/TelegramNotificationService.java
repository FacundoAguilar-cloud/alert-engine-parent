package saas.app.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
public class TelegramNotificationService {

    private final String botToken;
    private final String chatId;
    private final RestTemplate restTemplate = new RestTemplate();


    public TelegramNotificationService(@Value("${TELEGRAM_BOT_TOKEN}") String botToken,
                                       @Value("${TELEGRAM_CHAT_ID}") String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }


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
                            this.botToken, this.chatId, encodedMessage
                    );

                    restTemplate.getForObject(url, String.class);
                    log.info("Notificación de Telegram enviada para: {}", siteName);

        }catch (Exception e){
            log.error("Error enviando a Telegram: {}", e.getMessage());
        }
    }
}
