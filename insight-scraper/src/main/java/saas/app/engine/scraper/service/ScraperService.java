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

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ScraperService {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public ScraperData getLastestData(ProductLink link) {
        try {
            Document doc = Jsoup.connect(link.getUrl())
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();

            log.info("Título de página capturada: {}", doc.title());

            // intenta extraer el precio-
            BigDecimal extractedPrice = null;

            // Estrategia robusta para páginas grandes-
            Element scriptElement = doc.selectFirst("script[type=\"application/ld+json\"]");
            if (scriptElement != null) {
                String jsonContent = scriptElement.html();
                String priceVal = findValueInJson(jsonContent, "\"price\":");
                if (priceVal != null) {
                    extractedPrice = new BigDecimal(priceVal);
                    log.info("Precio hallado vía JSON-LD: {}", extractedPrice);
                }
            }

            // Otra estrategia por si la primera falla
            if (extractedPrice == null) {
                Element metaPrice = doc.selectFirst("meta[property=\"product:price:amount\"]");
                if (metaPrice != null) {
                    extractedPrice = PriceParser.parse(metaPrice.attr("content"));
                    log.info("Precio hallado vía Meta Tag: {}", extractedPrice);
                }
            }

            // Selector tradicional
            if (extractedPrice == null && link.getPriceSelector() != null) {
                Element priceElement = doc.selectFirst(link.getPriceSelector());
                if (priceElement != null) {
                    extractedPrice = PriceParser.parse(priceElement.text());
                    log.info("Precio hallado vía Selector CSS: {}", extractedPrice);
                }
            }

            // Si no hay precio, salimos
            if (extractedPrice == null) {
                log.error("No se encontró el precio en ninguna de las fuentes para: {}", link.getUrl());
                return null;
            }

            // Extracción de cuotas
            Integer installments = 1;
            if (link.getInstallmentsSelector() != null && !link.getInstallmentsSelector().isEmpty()) {
                Element instElement = doc.selectFirst(link.getInstallmentsSelector());
                if (instElement != null) {
                    installments = parseInstallments(instElement.text());
                }
            }

            // Lógica para el envio gratis.
            boolean isFree = false;
            if (Boolean.TRUE.equals(link.getHasFreeShipping())) {
                isFree = true;
            } else if (link.getFreeShippingThreshold() != null) {
                isFree = extractedPrice.compareTo(link.getFreeShippingThreshold()) >= 0;
            }

            // Retornamos la data
            return ScraperData.builder()
                    .price(extractedPrice)
                    .installments(installments)
                    .offersFreeShipping(isFree)
                    .build();

        } catch (Exception e) {
            log.error("Error de conexión o procesamiento con la URL {}: {}", link.getUrl(), e.getMessage());
            return null;
        }
    }

    private Integer parseInstallments(String text) {
        try {
            String numberOnly = text.replaceAll("[^0-9]", "");
            return numberOnly.isEmpty() ? 1 : Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 1;
        }
    }

    private String findValueInJson(String json, String key) {
        if (!json.contains(key)) return null;
        try {
            int start = json.indexOf(key) + key.length();
            // Buscamos el final del valor (puede ser una coma, una llave o un corchete)
            int end = json.length();
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == ',' || c == '}' || c == ']') {
                    end = i;
                    break;
                }
            }
            return json.substring(start, end).replace("\"", "").trim();
        } catch (Exception e) {
            return null;
        }
    }
}
