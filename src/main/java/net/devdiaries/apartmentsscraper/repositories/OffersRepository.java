package net.devdiaries.apartmentsscraper.repositories;

import net.devdiaries.apartmentsscraper.models.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OffersRepository extends CrudRepository<Offer, Long> {

    Optional<Offer> findOfferByUrl(String url);

}
