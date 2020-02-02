//package net.devdiaries.apartmentsscraper.services;
//
//import net.devdiaries.apartmentsscraper.models.Offer;
//import net.devdiaries.apartmentsscraper.repositories.OffersRepository;
//import net.devdiaries.apartmentsscraper.scrapers.olx.OLXScrapper;
//import net.devdiaries.apartmentsscraper.services.helpers.HttpStatusExtractor;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class PriceCheckerTest {
//
//    @Mock
//    OffersRepository offersRepository;
//
//    @Mock
//    OLXScrapper olxScrapper;
//
//    @Mock
//    HttpStatusExtractor httpStatusExtractor;
//
//    @Autowired
//    PriceChecker priceChecker;
//
//    @DisplayName("Should update offer price is price changed")
//    @Test
//    void shouldUpdatePrice() {
////        given
//        when(offersRepository.findAll()).thenReturn(createOffers());
//        when(olxScrapper.extractOfferData("olx.pl/offer1")).thenReturn(Offer.builder().price(new BigDecimal(80000)).build());
//        when(httpStatusExtractor.apply(anyString())).thenReturn(200);
////        when
//        priceChecker.checkOffersPrices();
////        then
//        Mockito.verify(offersRepository, Mockito.times(1)).save(any());
//
//    }
//
//    private List<Offer> createOffers() {
//        return
//                List.of(
//                        Offer.builder().url("olx.pl/offer1")
//                                .price(new BigDecimal(100000))
//                                .build(),
//                        Offer.builder().url("otodom.pl/offer1")
//                                .price(new BigDecimal(50000))
//                                .build()
//                );
//    }
//}
