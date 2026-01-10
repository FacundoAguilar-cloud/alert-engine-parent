package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;

import java.math.BigDecimal;

public class TiendaNubeExtractor implements PlatformExtractor {
    @Override
    public boolean supports(StorePlatform platform) {
        return platform == StorePlatform.TIENDANUBE;
    }

    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {
        Element priceMeta = doc.selectFirst("meta[property=\"product:price:amount\"]");

        BigDecimal price = null;

        if (priceMeta != null){
            price = PriceParser.parse(priceMeta.attr("content"));
        }
        //plan B por si falla la etiqueta de precio meta
        if (priceMeta == null && link.getPriceSelector() != null){
            Element el = doc.selectFirst(link.getPriceSelector());

            if (el != null) price = PriceParser.parse(el.text());
        }
        return ExtractorResult.builder()
                .price(price)
                .installments(1) //tiendanube requiere un selector manual para las cuotas, por eso lo dejamos asi tal cual
                .build();
    }
}
