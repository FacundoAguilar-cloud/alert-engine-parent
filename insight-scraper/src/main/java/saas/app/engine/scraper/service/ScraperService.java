package saas.app.engine.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.dto.ScraperData;
import saas.app.engine.scraper.extractor.PlatformExtractor;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScraperService {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private  final List <PlatformExtractor> extractors;

    public ScraperData getLastestData(ProductLink link) {
        try {
            Document doc = Jsoup.connect(link.getUrl())
                    .userAgent(USER_AGENT)
                    .timeout(15000)
                    .get();

            // detectamos la plataforma
            StorePlatform platform = detectPlatform(doc,link.getUrl());
            log.info("Analizando tienda {} con estrategia: {}", link.getStoreName(), platform);

            // Buscar la plataforma
            PlatformExtractor extractor = findExtractor(platform);

            //  Ejecutar extracción
            ExtractorResult result = extractor.extract(doc, link);

            if (result == null || result.getPrice() == null){
                log.error("El extractor {} no pudo obtener los datos de {}", platform, link.getUrl());
                return null;
            }

            boolean freeShipping = calculateFreeShipping(result.getPrice(), link);

            return ScraperData.builder()
                    .price(result.getPrice())
                    .installments(result.getInstallments())
                    .hasFreeShipping(freeShipping)
                    .build();
        }catch (Exception e){
            log.error("Error crítico en el Scraper para {}: {} ", link.getUrl(), e.getMessage());
            return null;
        }


}

    private StorePlatform detectPlatform(Document doc, String url){
        String html = doc.html().toLowerCase();
        String urlLower = url.toLowerCase();

        if (html.contains("vtex") ||
                html.contains("vtex-io") ||
                html.contains("vtex-apps-framework") ||
                 html.contains("vteximg")) {
            return StorePlatform.VTEX;
        }

        if (url.contains("dexter.com.ar") || url.contains("adidas.com.ar")){
            return StorePlatform.VTEX;
        }


        if (html.contains("tiendanube") || html.contains("nuvemshop") || urlLower.contains("tiendanube.com"))  {
            return StorePlatform.TIENDANUBE;
        }

        if (html.contains("shopify")){
            return  StorePlatform.SHOPIFY;
        }

        return StorePlatform.GENERIC;
    }

    private PlatformExtractor findExtractor(StorePlatform platform){
        return extractors.stream().filter(e -> e.supports(platform)
                ).findFirst().orElseGet(()-> {log.warn("No se encontró extractor específico para {}, usando genérico", platform);
            return extractors.stream().filter( e -> e.supports(StorePlatform.GENERIC)).findFirst()
                    .orElseThrow(()-> new RuntimeException("No hay extractores configurados"));

    });

}

    private boolean calculateFreeShipping(BigDecimal price, ProductLink link){
        if (Boolean.TRUE.equals(link.getHasFreeShipping())){
            return true;
        }

        if (link.getFreeShippingThreshold() !=null){
            return price.compareTo(link.getFreeShippingThreshold())  >= 0;
        }
        return false;
    }


}
