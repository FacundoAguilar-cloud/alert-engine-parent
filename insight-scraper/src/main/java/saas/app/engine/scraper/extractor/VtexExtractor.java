package saas.app.engine.scraper.extractor;

import lombok.val;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlataform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;
import saas.app.engine.scraper.util.ScraperUtils;

import java.math.BigDecimal;
@Component
public class VtexExtractor implements PlatformExtractor{
    @Override
    public boolean supports(StorePlataform platform) {
        return platform == StorePlataform.VTEX;
    }


    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {
        Element scriptElement = doc.selectFirst("script[type=\"application/ld+json\"]");
        BigDecimal price = null;

        if (scriptElement != null){
            String jsonContent = scriptElement.html();

            String priceVal = ScraperUtils.findValueInJson(jsonContent, "\"price\":");

            if (priceVal != null){
                price = new BigDecimal(priceVal);
            }
        }

        if (price == null){
            Element metaPrice = doc.selectFirst("\"meta[property=\"product:price:amount\"]\"");
            if (metaPrice != null){
                price = PriceParser.parse(metaPrice.attr("content"));
            }
        }



        Integer inst = 1;
        if (link.getInstallmentsSelector() != null && link.getInstallmentsSelector().isEmpty()){
            Element el = doc.selectFirst(link.getInstallmentsSelector());
            if (el == null){
                inst = ScraperUtils.parseInstallments(el.text());
            }
        }
        return  ExtractorResult
                .builder()
                .price(price)
                .installments(inst)
                .isAvailable(true)
                .build();
    }







}
