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
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            Element element = doc.selectFirst(cssSelector);
            return  (element != null) ? element.text() : "No encontrado";
        } catch (IOException e){
            return  "Error de conexión al intentar conectar con la URL" + url + "Detalle:" +  e.getMessage();
        }

    }
}
