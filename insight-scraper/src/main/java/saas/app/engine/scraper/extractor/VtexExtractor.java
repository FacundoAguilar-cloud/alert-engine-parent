package saas.app.engine.scraper.extractor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import saas.app.core.domain.ProductLink;
import saas.app.core.dto.SizeStockDTO;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;
import saas.app.engine.scraper.util.PriceParser;
import saas.app.engine.scraper.util.ScraperUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class VtexExtractor implements PlatformExtractor{
    @Override
    public boolean supports(StorePlatform platform) {
        return platform == StorePlatform.VTEX;
    }


    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {
        Element scriptElement = doc.selectFirst("script[type=\"application/ld+json\"]");
        BigDecimal price = null;
        String img = null;
        List<SizeStockDTO> sizes = new ArrayList<>();



        if (scriptElement != null){
            String jsonContent = scriptElement.html();

            String priceVal = ScraperUtils.findValueInJson(jsonContent, "\"price\":");

            if (priceVal != null){
                price = new BigDecimal(priceVal);
            }

            String rawImg = ScraperUtils.findValueInJson(jsonContent, "\"image\":");
            if (rawImg != null) img = rawImg
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "").trim();


            log.info("Imagen hallada via JSON-LD: {}", img);

            sizes = ScraperUtils.parseSizesFromJsonLD(jsonContent);
        }

        if (img == null || img.isEmpty() || img.equals("null") || img.contains("[") ){
            log.info("JSON-LD no proporcionó imagen válida, intentando Plan B (Meta Tags)...");
            img = ScraperUtils.extractImageUrl(doc);
        }

        if (price == null){
            Element metaPrice = doc.selectFirst("meta[property='product:price:amount']");
            if (metaPrice != null){
                price = PriceParser.parse(metaPrice.attr("content"));
            }
        }

        Integer inst = 1;
        if (link.getInstallmentsSelector() != null && !link.getInstallmentsSelector().isEmpty()){
            Element el = doc.selectFirst(link.getInstallmentsSelector());
            if (el != null){
                inst = ScraperUtils.parseInstallments(el.text());
            }
        }

        return  ExtractorResult
                .builder()
                .price(price)
                .installments(inst)
                .imageUrl(img)
                .sizes(sizes)
                .isAvailable(!sizes.isEmpty())
                .build();
    }







}
