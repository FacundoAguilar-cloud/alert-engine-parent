package saas.app.engine.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "saas.app")
@EnableScheduling
@EntityScan(basePackages = "saas.app.core.domain")
@EnableJpaRepositories(basePackages = "saas.app.core.repository")
public class ScraperWorkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScraperWorkingApplication.class, args);
    }
}
