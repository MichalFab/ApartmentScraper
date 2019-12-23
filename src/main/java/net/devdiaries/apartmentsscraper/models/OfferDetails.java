package net.devdiaries.apartmentsscraper.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OfferDetails {

    private String area;
    private BigDecimal m2Price;
    private Integer roomNumber;
    private Boolean isPrivate;
    private Integer floor;
    private Integer year;
    private BigDecimal rentPayment;
    private String district;

}
