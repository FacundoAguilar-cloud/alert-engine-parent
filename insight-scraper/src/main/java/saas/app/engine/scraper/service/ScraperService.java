package saas.app.engine.scraper.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ScraperService {

    public String getElementText(String url, String cssSelector){
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Element element = doc.selectFirst(doc.cssSelector());
            return  (element != null) ? element.text() : "No encontrado";
        } catch (IOException e){
            return  "Error de conexión" + e.getMessage();
        }

    }
}
