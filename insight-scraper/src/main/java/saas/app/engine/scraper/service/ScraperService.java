package saas.app.engine.scraper.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import saas.app.core.domain.ProductLink;
import saas.app.core.dto.ProductUpdateEvent;
import saas.app.core.enums.StorePlataform;
import saas.app.engine.scraper.dto.ScraperData;
import saas.app.engine.scraper.util.PriceParser;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ScraperService {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";


    private StorePlataform detectPlataform(Document doc){
        String html = doc.html().toLowerCase();

        if (html.contains("vtex") || html.contains("vtex-io")) {
            return StorePlataform.VTEX;
        }
        if (html.contains("tiendanube") || html.contains("nuvemshop")){
            return StorePlataform.TIENDANUBE;
        }

        if (html.contains("shopify")){
            return  StorePlataform.SHOPIFY;
        }

        return StorePlataform.GENERIC;
    }
    public ScraperData getLastestData(ProductLink link) {
        try {
            Document doc = Jsoup.connect(link.getUrl())
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();

            StorePlataform plataform = detectPlataform(doc);
            log.info("Plataforma detectada: {} para la URL: {}", plataform, link.getUrl());

            // intenta extraer el precio-
            BigDecimal price = null;

            switch (plataform){
                case VTEX:
                    price = extractPriceViaJsonLD(doc); //estos son los metodos que tenemos que crear
                    break;
                case TIENDANUBE:
                    price = extractPriceViaMeta(doc, "product:price:amount");
                    break;
                case GENERIC:
                default:
                    price = extractPriceViaSelector(doc, link.getPriceSelector());
                    break;
            }

            if (price == null){
                log.error("No se pudo extraer el precio de ninguna forma para {}",plataform);
                return null;
            }

            return ScraperData.builder()
                    .price(price)
                    .installments(1)
                    .hasFreeShipping(false)
                    .build();


    } catch (Exception e){
            log.error("Error scrapeando {}", e.getMessage());
            return null;
        }


}

}
