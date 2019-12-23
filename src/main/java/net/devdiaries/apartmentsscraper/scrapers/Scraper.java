package net.devdiaries.apartmentsscraper.scrapers;

import net.devdiaries.apartmentsscraper.models.Offer;

import java.util.Set;

public interface Scraper {

    Set<String> getAllOffersURLs(String city);
    Offer extractOfferData(String url);

}
