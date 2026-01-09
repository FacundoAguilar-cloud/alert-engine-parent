package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import saas.app.core.enums.StorePlataform;

import java.math.BigDecimal;

public interface PlatformExtractor {

    boolean supports(StorePlataform plataform);

    BigDecimal extractPrice (Document doc);
}
