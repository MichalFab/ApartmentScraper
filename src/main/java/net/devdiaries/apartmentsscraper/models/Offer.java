package net.devdiaries.apartmentsscraper.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "offers",
        indexes = {@Index(name = "url_index", columnList = "url", unique = true)})
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

    public Offer updateCity(String city) {
        this.city = city;
        return this;
    }

    public Offer updatePriceDown(Boolean isPriceDown) {
        this.isPriceDown = isPriceDown;
        return this;
    }

    public Offer updatePrice(BigDecimal price) {
        this.price = price;
        return this;
    }


    public String getUrl() {
        return url;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
