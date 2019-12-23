package net.devdiaries.apartmentsscraper.models;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "offers",
        indexes = {@Index(name = "url_index", columnList = "url", unique = true)})
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column
    private String offerService;

    @Column
    private String imageUrl;

    @Column
    private String title;

    @Column
    private String city;

    @Column
    private String district;

    @Column
    private String area;

    @Column
    @Lob
    private String textAbout;

    @Column
    private BigDecimal price;

    @Column
    private BigDecimal m2Price;

    @Column
    private Integer telNumber;

    @Column
    private String email;

    @Column
    private Integer roomNumber;

    @Column
    private Integer floor;

    @Column
    private Boolean isPrivate;

    @Column
    private Integer year;

    @Column
    private BigDecimal rentPayments;

    @Column
    private Boolean isPriceDown;

    public Offer updateCity(String city) {
        this.city = city;
        return this;
    }
}
