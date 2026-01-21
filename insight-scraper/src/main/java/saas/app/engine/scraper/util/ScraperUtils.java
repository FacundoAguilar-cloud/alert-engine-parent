package saas.app.engine.scraper.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import saas.app.core.dto.SizeStockDTO;

import java.util.ArrayList;
import java.util.List;

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
        try {
            JsonNode root = mapper.readTree(jsonContent);

            JsonNode offersArray = root.path("offers").path("offers");

            if (offersArray.isArray()){
                for (JsonNode offer: offersArray){
                    String name = offer.path("name").asText();
                    String availability = offer.path("availability").asText();

                    if (!name.isEmpty()){
                        boolean inStock = availability.contains("InStock");
                        sizes.add(new SizeStockDTO(name, inStock));
                    }
                }
            }
        } catch (Exception e){

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
    

}

