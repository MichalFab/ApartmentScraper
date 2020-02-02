package net.devdiaries.apartmentsscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApartmentsScraperApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApartmentsScraperApplication.class, args);
    }

}
