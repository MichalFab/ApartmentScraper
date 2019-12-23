package net.devdiaries.apartmentsscraper.services;

import net.devdiaries.apartmentsscraper.repositories.OffersRepository;
import net.devdiaries.apartmentsscraper.scrapers.Scraper;
import net.devdiaries.apartmentsscraper.scrapers.gratka.GratkaScraper;
import net.devdiaries.apartmentsscraper.scrapers.gumtree.GumtreeScraper;
import net.devdiaries.apartmentsscraper.scrapers.olx.OLXScrapper;
import net.devdiaries.apartmentsscraper.scrapers.otodom.OtodomScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OffersService {

    private final OffersRepository offersRepository;
    private final List<String> cities = List.of("wroclaw", "lublin", "lodz");
//    private final List<String> cities = List.of("wroclaw", "lublin", "lodz", "krakow", "warszawa", "opole", "rzeszow", "bialystok", "gdansk", "katowice", "kielce", "olsztyn", "poznan", "szczecin", "torun");

    @Autowired
    public OffersService(OffersRepository offersRepository) {
        this.offersRepository = offersRepository;
        saveNewOffers();
    }

    public void saveNewOffers() {
//        saveNewOlxOffers();
//        saveNewOtodomOffers();
//        saveNewGratkaOffers();
//        saveNewGumtreeOffers();
        GratkaScraper gratka = new GratkaScraper();
//        saveNewOffer(gratka);
//        saveNewOffer(new GumtreeScraper());
        saveNewOffer(new OLXScrapper());
//        saveNewOffer(new OtodomScraper());
    }

    private void saveNewOffer(Scraper scraper) {
        for(String city : cities) {
            scraper.getAllOffersURLs(city).stream()
                    .filter(url -> !url.isEmpty())
                    .map(scraper::extractOfferData)
                    .map(offer -> offer.updateCity(city))
                    .forEach(offersRepository::save);
        }
    }

    private void saveNewGumtreeOffers() {
        GumtreeScraper gumtreeScraper = new GumtreeScraper();
            cities.stream()
                .map(gumtreeScraper::getAllOffersURLs)
                .forEach(cityUrls -> cityUrls.stream()
                        .filter(this::isNewOffer)
                        .map(gumtreeScraper::extractOfferData)
                        .forEach(offersRepository::save));
    }

    private void saveNewOtodomOffers() {
        OtodomScraper otodomScraper = new OtodomScraper();
        cities.stream()
                .map(otodomScraper::getAllOffersURLs)
                .forEach(cityUrls -> cityUrls.stream()
                        .filter(this::isNewOffer)
                        .map(otodomScraper::extractOfferData)
                        .forEach(offersRepository::save));
    }

    private void saveNewOlxOffers() {
        OLXScrapper olxScrapper = new OLXScrapper();
        OtodomScraper otodomScraper = new OtodomScraper();
        cities.stream()
                .map(olxScrapper::getAllOffersURLs)
                .forEach(cityUrls -> cityUrls.stream()
                        .filter(this::isNewOffer)
                        .map(url -> url.startsWith("https://www.olx.pl") ? olxScrapper.extractOfferData(url) : otodomScraper.extractOfferData(url))
                        .forEach(offersRepository::save));
    }

    private void saveNewGratkaOffers() {
        GratkaScraper gratkaScraper = new GratkaScraper();
        cities.stream().map(gratkaScraper::getAllOffersURLs)
                .forEach(cityUrls -> cityUrls.stream()
                        .filter(this::isNewOffer)
                        .map(gratkaScraper::extractOfferData)
                        .forEach(offersRepository::save));
    }

    private Boolean isNewOffer(String url) {
        return offersRepository.findOfferByUrl(url).isEmpty();
    }
}
