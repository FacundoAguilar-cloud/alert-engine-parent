package saas.app.engine.scraper.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    public static String extractImageUrl(Document doc){
        Element metaImage = doc.selectFirst("meta[property=\"og:image\"]");

        if (metaImage != null){
            return metaImage.attr("content");
        }

        Element twitterImage = doc.selectFirst("meta[name=\"twitter:image\"]");

        if (twitterImage != null){
            return  twitterImage.attr("content");
        }

        return null;

    }
}
