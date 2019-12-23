package net.devdiaries.apartmentsscraper.scrapers.olx;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.models.OfferDetails;
import net.devdiaries.apartmentsscraper.scrapers.DocumentExtractor;
import net.devdiaries.apartmentsscraper.scrapers.Scraper;
import net.devdiaries.apartmentsscraper.scrapers.otodom.OtodomScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


public class OLXScrapper implements Scraper {

    private final static Logger LOGGER = Logger.getLogger(OLXScrapper.class.getName());
    private DocumentExtractor documentExtractor = new DocumentExtractor();
    private OtodomScraper otodomScraper = new OtodomScraper();

    public Set<String> getAllOffersURLs(String city) {
        Document doc = documentExtractor.apply("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/" + city);
        Elements elements = doc.select("a.detailsLink");
        return elements.stream().map(this::extractOfferURL).collect(Collectors.toSet());
    }

    private String extractOfferURL(Element el) {
        return el.attr("abs:href");
    }

    public Offer extractOfferData(String url) {
        if (url.startsWith("https://www.otodom.pl")) {
            return otodomScraper.extractOfferData(url);
        } else {
            LOGGER.info("Fetching data from " + url);
//        if (url.startsWith("https://www.olx.pl")) {
            Document doc = documentExtractor.apply(url);

            OfferDetails offerDetails = new OfferDetails();
            extractOfferDetails(offerDetails, doc.select(".details"));
            return Offer.builder()
                    .title(extractTitle(doc.select(".offer-titlebox")))
                    .url(url)
                    .offerService("Olx")
                    .area(offerDetails.getArea())
                    .roomNumber(offerDetails.getRoomNumber())
                    .floor(offerDetails.getFloor())
                    .m2Price(offerDetails.getM2Price())
                    .isPrivate(offerDetails.getIsPrivate())
                    .district(ofNullable(extractDistrict(doc.select(".show-map-link"))).orElse(null))
                    .price(extractPrice(doc.select(".price-label")))
                    .imageUrl(extractImageUrl(doc.select(".photo-glow")))
                    .textAbout(extractText(doc.select("#textContent")))
                    .build();
        }
    }

    private String extractDistrict(Elements locationDiv) {
        String[] locationParts = locationDiv.text().split(",");
        if (locationParts.length == 3) {
            return locationParts[2].replaceAll("Pokaż na mapie", "");
        }
        return null;
    }

    private String extractText(Elements textDiv) {
        return textDiv.text();
    }

    private String extractImageUrl(Elements photoDiv) {
        return photoDiv.select("img").attr("abs:src");
    }

    private BigDecimal extractPrice(Elements priceDiv) {
        String[] priceParts = priceDiv.select("strong").text().split(" ");
        return new BigDecimal(priceParts[0].concat(priceParts[1]));
    }

    private String extractTitle(Elements titleElement) {
        return titleElement.first().text();
    }

    private OfferDetails extractOfferDetails(OfferDetails offerDetails, Elements select) {
        Elements rows = select.select("tr");
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Element rowThElement = row.select("th").first();
            if (rowThElement != null) {
                if (rowThElement.text().equals("Oferta od")) {
                    if (row.select("td").select("a[href]").text().equals("Osoby prywatnej")) {
                        offerDetails.setIsPrivate(true);
                    } else {
                        offerDetails.setIsPrivate(false);
                    }
                }
                if (rowThElement.text().equals("Poziom")) {
                    String floor = row.select("td").select("a[href]").text().split(" ")[0];
                    try {
                        offerDetails.setFloor(floor.equals("Parter") ? 0 : Integer.valueOf(floor));
                    } catch (NumberFormatException ex) {
                        offerDetails.setFloor(0);
                    }
                }
                if (rowThElement.text().startsWith("Cena za")) {
                    offerDetails.setM2Price(new BigDecimal(row.select("td").select("strong").text().split(" ")[0]));
                }
                if (rowThElement.text().startsWith("Powierzchnia")) {
                    offerDetails.setArea(row.select("td").select("strong").text().split("")[0]);
                }
                if (rowThElement.text().equals("Liczba pokoi")) {
                    offerDetails.setRoomNumber(Integer.valueOf(row.select("td").select("a[href]").text().split(" ")[0]));
                }
            }

        }
        return offerDetails;
    }
}
