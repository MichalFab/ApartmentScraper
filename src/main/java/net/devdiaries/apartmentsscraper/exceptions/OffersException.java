package net.devdiaries.apartmentsscraper.exceptions;

import net.devdiaries.apartmentsscraper.models.Offer;

public class OffersException extends RuntimeException {

    private Offer offer;

    public OffersException() {

    }
    public OffersException(String message, Offer offer) {
        super(message);
        this.offer = offer;
    }
    public OffersException(String message, String info, Throwable cause) {
        super(message, cause);
        this.offer = offer;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " for the offer :" + offer;
    }

    @Override
    public String getLocalizedMessage() {
        return "The offer  " + offer + " is not available.";
    }
}
