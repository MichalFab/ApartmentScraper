package net.devdiaries.apartmentsscraper.scrapers.gumtree;

import net.devdiaries.apartmentsscraper.models.Offer;
import net.devdiaries.apartmentsscraper.models.OfferDetails;
import net.devdiaries.apartmentsscraper.scrapers.DocumentExtractor;
import net.devdiaries.apartmentsscraper.scrapers.Scraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GumtreeScraper implements Scraper {

    private static final Logger LOG = LoggerFactory.getLogger(GumtreeScraper.class);
    private DocumentExtractor documentExtractor = new DocumentExtractor();

    private Map<String, String> cityUrls = Map.ofEntries(new AbstractMap.SimpleEntry<>("wroclaw", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/wroclaw/v1c9073l3200114p1"),
            new AbstractMap.SimpleEntry<>("torun", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/torun/v1c9073l3200132p1"),
            new AbstractMap.SimpleEntry<>("lublin", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/lublin/v1c9073l3200145p1"),
            new AbstractMap.SimpleEntry<>("lodz", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/lodz/v1c9073l3200183p1"),
            new AbstractMap.SimpleEntry<>("krakow", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/krakow/v1c9073l3200208p1"),
            new AbstractMap.SimpleEntry<>("warszawa", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/warszawa/v1c9073l3200008p1"),
            new AbstractMap.SimpleEntry<>("opole", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/opole/v1c9073l3200234p1"),
            new AbstractMap.SimpleEntry<>("rzeszow", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/rzeszow/v1c9073l3200252p1"),
            new AbstractMap.SimpleEntry<>("bialystok", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/bialystok/v1c9073l3200259p1"),
            new AbstractMap.SimpleEntry<>("gdansk", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/gdansk/v1c9073l3200072p1"),
            new AbstractMap.SimpleEntry<>("katowice", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/katowice/v1c9073l3200285p1"),
            new AbstractMap.SimpleEntry<>("kielce", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/kielce/v1c9073l3200311p1"),
            new AbstractMap.SimpleEntry<>("olsztyn", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/olsztyn/v1c9073l3200338p1"),
            new AbstractMap.SimpleEntry<>("poznan", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/poznan/v1c9073l3200366p1"),
            new AbstractMap.SimpleEntry<>("szczecin", "https://www.gumtree.pl/s-mieszkania-i-domy-sprzedam-i-kupie/szczecin/v1c9073l3200402p1"));

    @Override
    public Set<String> getAllOffersURLs(String city) {
        Document doc = documentExtractor.apply(cityUrls.get(city));
        Elements elements = doc.select(".tile-title-text");
        return elements.stream().map(this::extractOfferURL).collect(Collectors.toSet());
    }

    @Override
    public Offer extractOfferData(String url) {
        LOG.info("Fetching data from " + url);
        Document doc = documentExtractor.apply(url);
        OfferDetails offerDetails = new OfferDetails();
        try {
            BigDecimal price = extractPrice(doc.select(".amount").first());
            extractOfferDetails(offerDetails, doc.select(".selMenu"), price);
            return Offer.builder()
                    .url(url)
                    .title(doc.select(".myAdTitle").text())
                    .offerService("Gumtree")
                    .price(price)
                    .imageUrl(extractImageUrl(doc.select(".vip-gallery")))
                    .isPrivate(offerDetails.getIsPrivate())
                    .roomNumber(offerDetails.getRoomNumber())
                    .area(offerDetails.getArea())
                    .m2Price(offerDetails.getM2Price())
                    .city(url.split("/")[4])
                    .textAbout(extractText(doc.select(".description")))
                    .build();
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    private String extractText(Elements textDiv) {
        return textDiv.select("span").first().text();
    }

    private String extractImageUrl(Elements galleryDiv) {
        return galleryDiv.select("img").attr("abs:src");
    }

    private BigDecimal extractPrice(Element priceDiv) {
        return new BigDecimal(priceDiv.text().replaceAll("[^\\d.]", ""));
    }

    private void extractOfferDetails(OfferDetails offerDetails, Elements detailsDiv, BigDecimal price) {
        Elements details = detailsDiv.select("li");
        offerDetails.setIsPrivate(true);
        for (Element detail : details) {
            if (detail.text().equals("Na sprzedaż przezAgencja")) {
                offerDetails.setIsPrivate(false);
            }
            if (detail.select(".name").text().equals("Liczba pokoi")) {
                try {
                    offerDetails.setRoomNumber(Integer.valueOf(detail.select(".value").text().split(" ")[0]));
                } catch (NumberFormatException ex) {
                    LOG.error("Cannot parse rooms number");
                }
            }
            if (detail.select(".name").text().equals("Wielkość (m2)")) {
                String area = detail.select("li").select(".value").text();
                try {
                    offerDetails.setArea(area);
                    offerDetails.setM2Price(price.divide(new BigDecimal(area), RoundingMode.CEILING));
                } catch (NumberFormatException ex) {
                    LOG.error("Cannot parse M2 price");
                }
            }
        }

    }

    private String extractOfferURL(Element el) {
        return el.attr("abs:href");
    }
}
