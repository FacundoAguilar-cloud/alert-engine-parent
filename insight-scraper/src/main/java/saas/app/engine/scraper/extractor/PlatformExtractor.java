package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;

public interface PlatformExtractor {

    boolean supports(StorePlatform platform);

    ExtractorResult extract (Document doc, ProductLink link);
}
