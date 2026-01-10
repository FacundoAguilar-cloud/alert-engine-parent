package saas.app.engine.scraper.extractor;

import org.jsoup.nodes.Document;
import saas.app.core.domain.ProductLink;
import saas.app.core.enums.StorePlatform;
import saas.app.engine.scraper.dto.ExtractorResult;

public class ShopifyExtractor implements PlatformExtractor {
    @Override
    public boolean supports(StorePlatform platform) {
        return false;
    }

    @Override
    public ExtractorResult extract(Document doc, ProductLink link) {
        return null;
    }
}
