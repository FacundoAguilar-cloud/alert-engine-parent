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
        Elements scripts = doc.select("script");
        BigDecimal price = null;
        String img = null;
        List<SizeStockDTO> sizes = new ArrayList<>();


        log.info("Bloques JSON-LD encontrados: {}", scripts.size());

        for (Element script : scripts){
            String content = script.html();

            if (content.contains("\"@type\"") || content.contains("__STATE__")){

                String priceVal = ScraperUtils.findValueInJson(content, "\"price\":");
                if (priceVal != null && price == null){
                    price = new BigDecimal(priceVal);
                    log.info("Precio hallado en el JSON {}",price);
                }

                List<SizeStockDTO> foundSizes = ScraperUtils.parseSizesFromJsonLD(content);

                if (!foundSizes.isEmpty()){
                    sizes = foundSizes;

                    log.info("Talles capturados por el Jackson: {} ", sizes.size());
                }

                //utilizamos el método nuevo para extraer el vtex-io si es que se está utilizando
                if (sizes.isEmpty() && content.contains("__STATE__")){
                    sizes = ScraperUtils.extractSizeFromVtexState(content);
                }

                if (price != null && !sizes.isEmpty()){
                    break;
                }


            }

            }

        img = ScraperUtils.extractImageUrl(doc);


        if (price == null){
            Element metaPrice = doc.selectFirst("meta[property='product:price:amount']");
            if (metaPrice != null){
                price = PriceParser.parse(metaPrice.attr("content"));
                log.info("Precio obtenido vía Meta Tag (Plan B): {}", price);
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
                .isAvailable(price != null &&  (!sizes.isEmpty() || img != null ))
                .build();
    }
        }














