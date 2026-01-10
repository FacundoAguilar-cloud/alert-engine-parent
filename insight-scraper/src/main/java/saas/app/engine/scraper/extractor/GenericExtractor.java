package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;
import saas.app.engine.scraper.util.ScraperUtils;

import java.math.BigDecimal;

@Component
public class GenericExtractor implements PlatformExtractor{
    @Override
    public boolean supports(StorePlatform platform) {
        return platform == StorePlatform.GENERIC;
    }

    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {

        BigDecimal price = null;

        if (link.getPriceSelector() != null && !link.getPriceSelector().isEmpty()){
            Element priceElement = doc.selectFirst(link.getPriceSelector());
            if (priceElement == null){
                price = PriceParser.parse(priceElement.text());
            }
        }
        Integer instalmments = 1;
        if (link.getInstallmentsSelector() != null && !link.getInstallmentsSelector().isEmpty()){
            Element instElement = doc.selectFirst(link.getInstallmentsSelector());

            if (instElement == null){
                instalmments = ScraperUtils.parseInstallments(instElement.text());
            }
        }
        return ExtractorResult.builder()
                .price(price)
                .installments(instalmments)
                .build();

    }



}
