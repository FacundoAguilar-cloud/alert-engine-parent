package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlataform;
import saas.app.engine.scraper.dto.ExtractorResult;

import java.math.BigDecimal;

public interface PlatformExtractor {

    boolean supports(StorePlataform platform);

    ExtractorResult extract (Document doc, ProductLink link);
}
