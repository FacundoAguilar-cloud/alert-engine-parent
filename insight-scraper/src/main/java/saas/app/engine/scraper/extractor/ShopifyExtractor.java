package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import saas.app.core.domain.ProductLink;
import saas.app.core.dto.SizeStockDTO;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;
import saas.app.engine.scraper.util.ScraperUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShopifyExtractor implements PlatformExtractor {
    @Override
    public boolean supports(StorePlatform platform) {
        return platform == StorePlatform.SHOPIFY;
    }

    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {
        Element scriptElement = doc.selectFirst("script[type=\"application/ld+json\"]");
        BigDecimal price = null;
        List<SizeStockDTO> sizes = new ArrayList<>();

        Element priceMeta = doc.selectFirst("meta[property=\"og:price:amount\"]");

        if (priceMeta != null){
            price = PriceParser.parse(priceMeta.attr("content"));
        }

        if (scriptElement != null){
           String json = scriptElement.html();



            if (price == null){
                String priceVal = ScraperUtils.findValueInJson(json, "\"price\":");

                if (priceVal != null) price = new BigDecimal(priceVal);
            }
            sizes = ScraperUtils.parseSizesFromJsonLD(json);
        }

        String img = ScraperUtils.extractImageUrl(doc);


        return ExtractorResult.builder()
                .price(price)
                .installments(1)
                .sizes(sizes)
                .imageUrl(img)
                .isAvailable(!sizes.isEmpty())
                .build();


    }
}
