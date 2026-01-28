package saas.app.core.util;

public class UrlUtils {

    public static String cleanStoreUrl(String url){
        if (url == null || !url.contains("?")){
            return url;
        }
        return url.substring(0, url.indexOf("?"));
    }
}
