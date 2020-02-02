package net.devdiaries.apartmentsscraper.services;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.repositories.OffersRepository;
import net.devdiaries.apartmentsscraper.services.helpers.HttpStatusExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ExpiredOffersRemover {

    private final static Logger LOGGER = Logger.getLogger(ExpiredOffersRemover.class.getName());
    private final OffersRepository offersRepository;

    @Autowired
    public ExpiredOffersRemover(OffersRepository offersRepository) {
        this.offersRepository = offersRepository;
    }

    @Scheduled(cron = "0 0 */22 * * *")
    public void checkOffersUrls() {
        LOGGER.info("Checking expired offers");
        offersRepository.findAll().forEach(this::removeIfOfferNotExists);
    }

    private void removeIfOfferNotExists(Offer offer) {
        HttpStatusExtractor httpStatusExtractor = new HttpStatusExtractor();
        if (httpStatusExtractor.apply(offer.getUrl()) == 301) {
            LOGGER.info("Deleting expired offer: " + offer.getUrl());
            offersRepository.delete(offer);
        }
    }
}
