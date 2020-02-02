package net.devdiaries.apartmentsscraper.scrapers.otodom;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.models.OfferDetails;
import net.devdiaries.apartmentsscraper.scrapers.DocumentExtractor;
import net.devdiaries.apartmentsscraper.scrapers.Scraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

public class OtodomScraper implements Scraper {

    private static final Logger LOG = LoggerFactory.getLogger(OtodomScraper.class);
    private DocumentExtractor documentExtractor = new DocumentExtractor();

    @Override
    public Set<String> getAllOffersURLs(String city) {
        Document doc = documentExtractor.apply("https://www.otodom.pl/sprzedaz/mieszkanie/" + city);
        Elements elements = doc.select(".offer-item-header");
        return elements.stream().map(this::extractOfferURL).collect(Collectors.toSet());
    }

    private String extractOfferURL(Element el) {
        return el.select("h3").select("a").attr("href");
    }

    @Override
    public Offer extractOfferData(String url) {
        LOG.info("Fetching data from " + url);
        Document doc = documentExtractor.apply(url);
        OfferDetails offerDetails = new OfferDetails();
        try {
            extractOfferDetails(offerDetails, doc.select(".section-overview"));
            return Offer.builder()
                    .url(url)
                    .title(extractTitle(doc.select(".css-1ld8fwi")))
                    .offerService("Otodom")
                    .floor(offerDetails.getFloor())
                    .price(extractPrice(doc.select(".css-1vr19r7")))
                    .m2Price(extractM2Price(doc.select(".css-18q4l99")))
                    .roomNumber(offerDetails.getRoomNumber())
                    .year(offerDetails.getYear())
                    .area(offerDetails.getArea())
                    .rentPayments(offerDetails.getRentPayment())
                    .textAbout(extractText(doc.select(".section-description")))
                    .isPrivate(extractIsPrivate(doc.select(".css-8xerhz-Pe")))
                    .imageUrl(extractImageUrl(doc.select(".slick-slider")))
                    .build();
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    private String extractImageUrl(Elements imgDiv) {
        return imgDiv.select("img").attr("abs:src");
    }

    private Boolean extractIsPrivate(Elements isPrivateSelect) {
        return isPrivateSelect.text().equals("Oferta prywatna");
    }

    private String extractText(Elements textAboutClass) {
        StringBuilder textAbout = new StringBuilder();
        Elements allTextRows = textAboutClass.select("p");
        allTextRows.forEach(row -> textAbout.append(row.text()));
        return textAbout.toString();
    }

    private BigDecimal extractM2Price(Elements m2Price) {
        String price = m2Price.text().split(" ")[0].concat(m2Price.text().split(" ")[1]);
        return new BigDecimal(price);
    }

    private BigDecimal extractPrice(Elements priceClass) {
        return new BigDecimal(priceClass.text().split(" ")[0].concat(priceClass.text().split(" ")[1].replace(",", "")));
    }

    private String extractTitle(Elements titleClass) {
        return titleClass.text();
    }

    private void extractOfferDetails(OfferDetails offerDetails, Elements detailsDiv) {
        Elements details = detailsDiv.select("li");
        for (Element detail : details) {
            if (detail.text().split(" ")[0].startsWith("Powierzchnia:")) {
                offerDetails.setArea(detailsDiv.select("li").first().select("strong").text().split(" ")[0]);
            }
            if (detail.text().startsWith("Liczba pokoi:")) {
                offerDetails.setRoomNumber(Integer.valueOf(detail.text().split(" ")[2]));
            }
            if (detail.text().startsWith("Piętro:")) {
                try {
                    offerDetails.setFloor(Integer.valueOf(detail.text().split(" ")[1]));
                } catch (NumberFormatException ex) {
                    offerDetails.setFloor(0);
                }
            }
            if (detail.text().startsWith("Rok budowy:")) {
                try {
                    offerDetails.setYear(Integer.valueOf(detail.text().split(" ")[2]));
                } catch (NumberFormatException ex) {
                    offerDetails.setYear(0);
                }
            }
            if (detail.text().equals("Czynsz:")) {
                offerDetails.setRentPayment(new BigDecimal(detailsDiv.select("li").first().select("strong").text().split(" ")[0]));
            }
        }
    }
}
