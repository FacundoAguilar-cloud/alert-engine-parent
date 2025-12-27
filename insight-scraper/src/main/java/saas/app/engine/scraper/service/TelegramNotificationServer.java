package saas.app.engine.scraper.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class TelegramNotificationServer {

    private static final String BOT_TOKEN = "7805759839:AAGXyWVqr7kdahUxKUK8wSslIwrAORdjxHw";
    private static final String CHAT_ID = "5581094202";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMeTelegramAlert(String siteName, String oldValue, String newValue){
        try {
            String message =String.format(

            )
        }
    }
}
