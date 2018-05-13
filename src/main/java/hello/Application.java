package hello;

import hello.storage.StorageProperties;
import hello.storage.StorageService;


import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.logging.Logger;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static final Logger logger = (Logger) LoggerFactory.getLogger(Application.class);
    public static final int APP_IDENTIFIER = 1;



    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            logger.info("application " + APP_IDENTIFIER + " started");
            storageService.deleteAll();
            storageService.init();
        };
    }
}