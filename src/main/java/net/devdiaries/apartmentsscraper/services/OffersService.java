package net.devdiaries.apartmentsscraper.services;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.repositories.OffersRepository;
import net.devdiaries.apartmentsscraper.scrapers.Scraper;
import net.devdiaries.apartmentsscraper.scrapers.gratka.GratkaScraper;
import net.devdiaries.apartmentsscraper.scrapers.gumtree.GumtreeScraper;
import net.devdiaries.apartmentsscraper.scrapers.olx.OLXScrapper;
import net.devdiaries.apartmentsscraper.scrapers.otodom.OtodomScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OffersService {

    private final OffersRepository offersRepository;
    private final List<String> cities = List.of("wroclaw", "lublin", "lodz", "krakow", "warszawa", "opole", "rzeszow", "bialystok", "gdansk", "katowice", "kielce", "olsztyn", "poznan", "szczecin", "torun");

    @Autowired
    public OffersService(OffersRepository offersRepository) {
        this.offersRepository = offersRepository;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void saveNewOffers() {
        saveNewOffer(new OLXScrapper());
        saveNewOffer(new OtodomScraper());
        saveNewOffer(new GumtreeScraper());
        saveNewOffer(new GratkaScraper());
    }

    private void saveNewOffer(Scraper scraper) {
        for (String city : cities) {
            scraper.getAllOffersURLs(city).stream()
                    .filter(url -> !url.isEmpty())
                    .map(scraper::extractOfferData)
                    .filter(Objects::nonNull)
                    .map(offer -> offer.updateCity(city))
                    .forEach(this::saveIfNotExists);
        }
    }

    private void saveIfNotExists(Offer offer) {
        Optional<Offer> fetchedOffer = offersRepository.findOfferByUrl(offer.getUrl());
        if (fetchedOffer.isEmpty()) {
            offersRepository.save(offer);
        } else {
            Offer offerUpdate = fetchedOffer.get();
            if (offerUpdate.getPrice().compareTo(offer.getPrice()) > 0) {
                offerUpdate.updatePriceDown(true);
                offerUpdate.updatePrice(offer.getPrice());
                offersRepository.save(offerUpdate);
            }
        }
    }
}
