package net.devdiaries.apartmentsscraper.services;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.repositories.OffersRepository;
import net.devdiaries.apartmentsscraper.scrapers.gratka.GratkaScraper;
import net.devdiaries.apartmentsscraper.scrapers.gumtree.GumtreeScraper;
import net.devdiaries.apartmentsscraper.scrapers.olx.OLXScrapper;
import net.devdiaries.apartmentsscraper.scrapers.otodom.OtodomScraper;
import net.devdiaries.apartmentsscraper.services.helpers.HttpStatusExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Service
public class PriceChecker {

    private final static Logger LOGGER = Logger.getLogger(PriceChecker.class.getName());
    private final OffersRepository offersRepository;

    @Autowired
    public PriceChecker(OffersRepository offersRepository) {
        this.offersRepository = offersRepository;
    }

    @Scheduled(cron = "0 0 */24 * * *")
    public void checkOffersPrices() {
        LOGGER.info("Checking offers prices");
        offersRepository.findAll().forEach(this::checkOfferPrice);
    }

    private void checkOfferPrice(Offer offer) {
        HttpStatusExtractor httpStatusExtractor = new HttpStatusExtractor();
        if (httpStatusExtractor.apply(offer.getUrl()) == 200) {
            BigDecimal fetchedPrice = fetchOfferPrice(offer.getUrl());
            if (fetchedPrice != null) {
                if (fetchedPrice.compareTo(offer.getPrice()) != 0) {
                    LOGGER.info("Updating offer: " + offer.getUrl() + " price from " + offer.getPrice());
                    offer.updatePriceDown(true);
                    offer.updatePrice(fetchedPrice);
                }
            }
        }
    }

    private BigDecimal fetchOfferPrice(String url) {
        if (url.contains("otodom")) {
            OtodomScraper otodomScraper = new OtodomScraper();
            return otodomScraper.extractOfferData(url).getPrice();
        }
        if (url.contains("gratka")) {
            GratkaScraper gratkaScraper = new GratkaScraper();
            return gratkaScraper.extractOfferData(url).getPrice();
        }

        if (url.contains("gumtree")) {
            GumtreeScraper gumtreeScraper = new GumtreeScraper();
            return gumtreeScraper.extractOfferData(url).getPrice();
        }
        if (url.contains("olx")) {
            OLXScrapper olxScrapper = new OLXScrapper();
            return olxScrapper.extractOfferData(url).getPrice();
        }
        return null;
    }
}
