package net.devdiaries.apartmentsscraper;

import net.devdiaries.apartmentsscraper.scrapers.DocumentExtractor;
import net.devdiaries.apartmentsscraper.scrapers.olx.OLXScrapper;
import net.devdiaries.apartmentsscraper.services.OffersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApartmentsScraperApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApartmentsScraperApplication.class, args);
//        OLXScrapper olxScrapper = new OLXScrapper(new DocumentExtractor());
    }

}
