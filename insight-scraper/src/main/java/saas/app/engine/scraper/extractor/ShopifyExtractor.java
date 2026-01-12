package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;
import saas.app.engine.scraper.util.ScraperUtils;

import java.math.BigDecimal;

public class ShopifyExtractor implements PlatformExtractor {
    @Override
    public boolean supports(StorePlatform platform) {
        return platform == StorePlatform.SHOPIFY;
    }

    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {
        Element priceMeta = doc.selectFirst("meta[property=\"og:price:amount\"]");

        BigDecimal price = null;

        if (priceMeta != null){
            price = PriceParser.parse(priceMeta.attr("content"));
        }

        if (priceMeta == null){
            Element scriptJson = doc.selectFirst("script[type=\"application/ld+json\"]");

            if (scriptJson != null){
                String priceVal = ScraperUtils.findValueInJson(scriptJson.html(), "\"price\":");

                if (priceVal != null) price = new BigDecimal(priceVal);
            }
        }

        String img = ScraperUtils.extractImageUrl(doc);

        return ExtractorResult.builder()
                .price(price)
                .installments(1)
                .imageUrl(img)
                .build();


    }
}
