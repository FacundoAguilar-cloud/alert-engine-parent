package saas.app.engine.insightapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "saas.app")
@EntityScan(basePackages = "saas.app.core.domain")
@EnableJpaRepositories(basePackages = "saas.app.core.repository")
public class InsightApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightApiApplication.class, args);
    }

}
