package saas.app.engine.scraper.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import saas.app.core.domain.ProductLink;
import saas.app.core.dto.ProductUpdateEvent;
import saas.app.engine.scraper.dto.ScraperData;
import saas.app.engine.scraper.util.PriceParser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ScraperService {
    //esto nos va a servir para que no nos bloqueen las páginas pensando que somos un bot
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public ScraperData getLastestData(ProductLink link){
        try {
            Document doc = Jsoup.connect(link.getUrl())
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();

            Element priceElement = doc.selectFirst(link.getPriceSelector());
            if (priceElement == null){
                log.error("No se encontró el precio en {} con el selector {}", link.getUrl(), link.getPriceSelector());
                return null;
            }
            BigDecimal price = PriceParser.parse(priceElement.text());

            Integer installments = 1;
            if (link.getInstallmnetsSelector() != null && !link.getInstallmnetsSelector().isEmpty()){
                Element isntElement = doc.selectFirst(link.getInstallmnetsSelector());
                if (isntElement != null){
                    installments = parseInstallsments(isntElement.text());
                }
            }
            //aca iria la logica del envio gratis (si es que hay y a partir de que monto)
            boolean isFree = false;
            if (link.getHasFreeShipping() != null && link.getHasFreeShipping()){
                isFree = true; //en este caso tendria envio gratis
            } else if (link.getFreeShippingThreshold() != null) {
                isFree = price.compareTo(link.getFreeShippingThreshold()) >= 0;
            }
            return ScraperData.builder()
                    .price(price)
                    .installments(installments)
                    .offersFreeShipping(isFree)
                    .build();
        }catch (Exception e){
            log.error("Error de conexión con la URL {}: {} ", link.getUrl(),e.getMessage());
            return null;
        }
    }

    private Integer parseInstallsments(String text){
        try {
            String numberOnly = text.replaceAll("[^0-9]", "");
            if (numberOnly.isEmpty()) return 1;

            return Integer.parseInt(numberOnly);
        }catch (Exception e){
            return 1;
        }
    }




}
