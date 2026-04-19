package com.kisan.repository;

import com.kisan.entity.MandiPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface MandiPriceRepository extends JpaRepository<MandiPrice, Long> {
    boolean existsByMandiNameAndCommodityAndPriceDate(String mandi, String commodity, LocalDate date);
}