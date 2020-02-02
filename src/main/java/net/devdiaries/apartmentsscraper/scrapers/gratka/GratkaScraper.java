package net.devdiaries.apartmentsscraper.scrapers.gratka;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.models.OfferDetails;
import net.devdiaries.apartmentsscraper.scrapers.DocumentExtractor;
import net.devdiaries.apartmentsscraper.scrapers.Scraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

public class GratkaScraper implements Scraper {

    private static final Logger LOG = LoggerFactory.getLogger(GratkaScraper.class);
    private DocumentExtractor documentExtractor = new DocumentExtractor();

    @Override
    public Set<String> getAllOffersURLs(String city) {
        Document doc = documentExtractor.apply("https://gratka.pl/nieruchomosci/mieszkania/" + city + "/sprzedaz");
        Elements elements = doc.select(".teaser__anchor");
        return elements.stream().map(this::extractOfferURL).collect(Collectors.toSet());
    }

    @Override
    public Offer extractOfferData(String url) {
        LOG.info("Fetching data from " + url);
        Document doc = documentExtractor.apply(url);
        OfferDetails offerDetails = new OfferDetails();
        try {
            extractOfferDetails(offerDetails, doc.select(".parameters__rolled"));
            return Offer.builder()
                    .url(url)
                    .title(doc.select(".sticker__title").text())
                    .textAbout(doc.select(".description__container").text())
                    .price(extractPrice(doc.select(".priceInfo__value")))
                    .m2Price(extractM2Price(doc.select(".priceInfo__additional")))
                    .district(offerDetails.getDistrict())
                    .floor(offerDetails.getFloor())
                    .year(offerDetails.getYear())
                    .area(offerDetails.getArea())
                    .offerService("Gratka")
                    .roomNumber(offerDetails.getRoomNumber())
                    .rentPayments(offerDetails.getRentPayment())
                    .imageUrl(extractImageUrl(doc.select(".gallery")))
                    .build();
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    private String extractOfferURL(Element el) {
        return el.attr("abs:href");
    }

    private String extractImageUrl(Elements imageDiv) {
        return imageDiv.select("img").attr("abs:src").replace("xsmall", "xlarge");
    }

    private BigDecimal extractPrice(Elements priceDiv) {
        String price = priceDiv.text().split(",")[0].replaceAll("\\s+", "");
        if (!price.chars().allMatch(Character::isDigit)) {
            return new BigDecimal(0);
        }
        return new BigDecimal(price);
    }

    private BigDecimal extractM2Price(Elements priceDiv) {
        String[] price = priceDiv.text().split(" ");
        price = Arrays.copyOf(price, price.length - 1);
        String resultPrice = "";
        for (String s : price) {
            resultPrice = resultPrice.concat(s);
        }
        if (resultPrice.isEmpty()) {
            return new BigDecimal(0);
        }
        return new BigDecimal(resultPrice.replaceAll(",", "."));
    }

    private void extractOfferDetails(OfferDetails offerDetails, Elements detailsDiv) {
        Elements details = detailsDiv.select("li");
        for (Element detail : details) {
            if (detail.text().split(" ")[0].equals("Powierzchnia")) {
                offerDetails.setArea(detail.select("b").text().split(" ")[0]);
            }
            if (detail.text().startsWith("Liczba pokoi")) {
                offerDetails.setRoomNumber(Integer.valueOf(detail.text().split(" ")[2]));
            }
            if (detail.text().startsWith("Piętro")) {
                String floor = detail.text().split(" ")[1];
                try {
                    offerDetails.setFloor(floor.equals("parter") ? 0 : Integer.valueOf(floor));
                } catch (NumberFormatException ex) {
                    offerDetails.setFloor(0);
                }
            }
            if (detail.text().startsWith("Rok budowy")) {
                offerDetails.setYear(Integer.valueOf(detail.text().split(" ")[2]));
            }
            if (detail.text().startsWith("Lokalizacja")) {
                offerDetails.setDistrict(detail.text().split(" ")[2]);
            }

            if (detail.text().startsWith("Opłaty")) {
                offerDetails.setRentPayment(new BigDecimal(detail.text().split(" ")[4].replaceAll(",", ".")));
            }
        }
    }
}
