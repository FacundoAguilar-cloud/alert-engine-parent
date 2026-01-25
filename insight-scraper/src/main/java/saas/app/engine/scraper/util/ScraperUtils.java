package saas.app.engine.scraper.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import saas.app.core.dto.SizeStockDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ScraperUtils {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());


    public static Integer parseInstallments(String text) { //este metodo lo movemos del ScraperService y lo traemos para aca
        try {
            String numberOnly = text.replaceAll("[^0-9]", "");
            return numberOnly.isEmpty() ? 1 : Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 1;
        }
    }

    public static String findValueInJson(String json, String key) {
        if (!json.contains(key)){
            key = key.replace(":", " :");
            if (!json.contains(key)) return null;
        }
        try {
            int start = json.indexOf(key) + key.length();
            // Buscamos el final del valor (puede ser una coma, una llave o un corchete)
            int end = json.length();
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == ',' || c == '}' || c == ']') {
                    end = i;
                    break;
                }
            }
            return json.substring(start, end).replace("\"", "").trim();
        } catch (Exception e) {
            return null;
        }
    }


    public static List <SizeStockDTO> parseSizesFromJsonLD(String jsonContent){
        List <SizeStockDTO> sizes = new ArrayList<>();

        if (jsonContent == null || jsonContent.isEmpty() ) return sizes;


        try {
            Pattern namePatern = Pattern.compile("\"skuName\":\"([^\"]+)\"");
            Matcher matcher = namePatern.matcher(jsonContent);

            while (matcher.find()){
                String sizeName = matcher.group(1);

                int startSearch = matcher.end();
                int endSearch = Math.min(startSearch + 500, jsonContent.length());

                String context = jsonContent.substring(startSearch, endSearch);

                boolean inStock = context.contains("InStock") || context.contains("true");

                if (sizeName.length() < 15 && !sizeName.equalsIgnoreCase("default")){
                    sizes.add(new SizeStockDTO(sizeName.trim(), inStock));
                }
            }
        } catch (Exception e){
            log.error("Error en extraccion por Regex: {}", e.getMessage());
        }
         return sizes.stream().collect(Collectors.toMap(SizeStockDTO::getSize, s -> s, (s1, s2) -> s1))
                 .values().stream().toList();


    }

    private static List <SizeStockDTO> extractSizesFromProductNode(JsonNode node){
        List <SizeStockDTO> sizes = new ArrayList<>();

        JsonNode offersField = node.path("offers");
        JsonNode mainArray = offersField.isArray() ? offersField : offersField.path("offers");

        if (mainArray.isArray()){
            for (JsonNode offer : mainArray){
                String name = offer.has("name") ? offer.get("name").asText() : offer.path("skuName").asText();

                String availability = offer.path("availability").asText();
                boolean inStock = availability.contains("InStock");

                if (!name.isEmpty() && name.length() < 15){
                    sizes.add(new SizeStockDTO(name.trim(),inStock));
                }
            }
        }
        return sizes;

    }


    public static String extractImageUrl(Document doc){

        String imageUrl = null;

        Element metaOgImage = doc.selectFirst("meta[property='og:image']");

        if (metaOgImage != null) imageUrl = metaOgImage.attr("content");

        if (imageUrl == null){
            Element metaTwitterImage = doc.selectFirst("meta[name='twitter:image']");

            if (metaTwitterImage != null) imageUrl = metaTwitterImage.attr("content");
        }



        if (imageUrl == null){
            Element metaSchemaImage = doc.selectFirst("meta[itemprop='image']");
            if (metaSchemaImage != null) imageUrl = metaSchemaImage.attr("content");
        }


        return cleanImageUrl(imageUrl);

    }

    private static String cleanImageUrl(String url){
        if (url == null || url.isEmpty()) return null;

        if (url.startsWith("//")) return "https:"  + url;

        return url;
    }
    // nuevo método para extraer el vtex-io y hacer incluso más robusto el scraping (terminar de hacer cambios)
    public static List <SizeStockDTO> extractSizeFromVtexState(String htmlContent){
        List<SizeStockDTO> sizes = new ArrayList<>();

        try {
            Pattern pattern = Pattern.compile("\"name\":\"([^\"]+)\",\"nameComplete\"");
            Matcher matcher = pattern.matcher(htmlContent);

            while (matcher.find()){
                String sizeName = matcher.group(1);
                String availability = matcher.group(2);

                if (sizeName.length() < 15){
                    sizes.add(new SizeStockDTO(sizeName, availability.contains("InStock")));
                }
            }
        } catch (Exception e){
            log.error("Error en rescate VTEX IO: {}", e.getMessage());
        }
        return sizes.stream().distinct().collect(Collectors.toList());
    }
    

}

