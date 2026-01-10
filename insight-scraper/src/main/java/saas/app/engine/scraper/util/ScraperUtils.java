package saas.app.engine.scraper.util;

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
}
