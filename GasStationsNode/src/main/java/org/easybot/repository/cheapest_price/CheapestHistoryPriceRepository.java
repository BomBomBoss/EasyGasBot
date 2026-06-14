package org.easybot.repository.cheapest_price;

import org.easybot.entity.cheapest_price.CheapestHistoryPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheapestHistoryPriceRepository extends JpaRepository<CheapestHistoryPrice, Long> {

    List<CheapestHistoryPrice> findByDateAfterOrderByDate(final LocalDate startDate);

}
