package saas.app.engine.scraper.extractor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;
import saas.app.engine.scraper.util.ScraperUtils;

import java.math.BigDecimal;

@Slf4j
@Component
public class GenericExtractor implements PlatformExtractor{
    @Override
    public boolean supports(StorePlatform platform) {
        return platform == StorePlatform.GENERIC;
    }




    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {

        BigDecimal price = null;

        Element priceElement = doc.selectFirst(link.getPriceSelector());

            if (priceElement != null){
                price = PriceParser.parse(priceElement.text());
            } else {
                log.warn("El selector manual {} no devolvió ningún resultado en {}", link.getPriceSelector(), link.getUrl());
            }

        Integer installments = 1;
        if (link.getInstallmentsSelector() != null && !link.getInstallmentsSelector().isEmpty()){
            Element instElement = doc.selectFirst(link.getInstallmentsSelector());

            if (instElement != null){
                installments = ScraperUtils.parseInstallments(instElement.text());
            } else {
                log.warn("No se encontró el elemento de coutas para el selector: {}", link.getInstallmentsSelector());
            }
        }

        String img = ScraperUtils.extractImageUrl(doc);


        return ExtractorResult.builder()
                .price(price)
                .installments(installments)
                .imageUrl(img)
                .build();

    }



}
