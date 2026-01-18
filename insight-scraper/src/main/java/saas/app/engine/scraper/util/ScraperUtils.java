package saas.app.engine.scraper.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import saas.app.core.dto.SizeStockDTO;

import java.util.ArrayList;
import java.util.List;

public class ScraperUtils {

    public static Integer parseInstallments(String text) { //este metodo lo movemos del ScraperService y lo traemos para aca
        try {
            String numberOnly = text.replaceAll("[^0-9]", "");
            return numberOnly.isEmpty() ? 1 : Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 1;
        }
    }

    public static String findValueInJson(String json, String key) {
        if (!json.contains(key)) return null;
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


    public static List <SizeStockDTO> parseSizesFromJsonLD(String json){
        List <SizeStockDTO> sizes = new ArrayList<>();

        if (json == null) return sizes;

        String keyword = "\"@type\"";  //terminar de arreglar mañana
        if (!json.contains(keyword)) return sizes;

        String[] blocks = json.split("\"@type\"");

        String[] offerBlocks = json.split("\"@type\":\"Offer\"");

        for (int i = 1 ; i < offerBlocks.length; i++){
            String block = offerBlocks[i];
            String sizeName = ScraperUtils.findValueInJson(block, "\"name\":");

            String availability = ScraperUtils.findValueInJson(block, "\"availability\":");

            if (sizeName != null){
                boolean inStock = availability != null && availability.contains("InStock");

                sizes.add(new SizeStockDTO(sizeName.trim(), inStock));
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
    

}
