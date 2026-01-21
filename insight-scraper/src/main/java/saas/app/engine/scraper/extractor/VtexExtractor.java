package saas.app.engine.scraper.extractor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        Elements scripts = doc.select("script[type='application/ld+json']");
        BigDecimal price = null;
        String img = null;
        List<SizeStockDTO> sizes = new ArrayList<>();

        log.info("Bloques JSON-LD encontrados: {}", scripts.size());

        for (Element script : scripts){
            String json = script.html();

            if (json.contains("\"@type\":\"Product\"")){
                sizes = ScraperUtils.parseSizesFromJsonLD(json);

                if (!sizes.isEmpty()){
                    log.info("¡Talles capturados con Jackson!: {}", sizes.size());

                    String priceVal = ScraperUtils.findValueInJson(json, "\"price\":");
                    if (priceVal != null ) price = new BigDecimal(priceVal);

                    break;
                }
            }

            }

        img = ScraperUtils.extractImageUrl(doc);
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

        log.info("Talles extraídos para {}: {}", link.getStoreName(), sizes.size()); //log para los talles antes del return
        for(SizeStockDTO s : sizes) {
            log.info("Talle: {} - Disponible: {}", s.getSize(), s.getAvailable());
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














