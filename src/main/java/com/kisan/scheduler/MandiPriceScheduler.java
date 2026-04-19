package com.kisan.scheduler;

import com.kisan.config.MandiRegistry;
import com.kisan.entity.MandiPrice;
import com.kisan.repository.MandiPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Nightly scheduler — runs at 12:00 AM IST.
 * Fetches mandi prices from data.gov.in Agmarknet API for all registered mandis.
 *
 * API endpoint: https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070
 * Fields: state, district, market, commodity, min_price, max_price, modal_price, arrival_date
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MandiPriceScheduler {

    private final MandiPriceRepository mandiPriceRepository;
    private final MandiRegistry mandiRegistry;
    private final RestTemplate restTemplate;

    @Value("${datagov.api.key}")
    private String apiKey;

    @Value("${datagov.api.base-url:https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070}")
    private String apiBaseUrl;

    // Commodities we care about in Goa-Karnataka-Maharashtra corridor
    private static final List<String> TARGET_COMMODITIES = List.of(
        "Cashew", "Tomato", "Onion", "Paddy(Dhan)(Common)", "Coconut"
    );

    /**
     * Runs every night at midnight IST.
     * cron = "second minute hour day month weekday"
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
    public void syncMandiPrices() {
        log.info("=== MandiPriceSync starting — {} ===", LocalDate.now());
        int totalSaved = 0;

        for (MandiRegistry.MandiInfo mandi : MandiRegistry.ALL_MANDIS) {
            for (String commodity : TARGET_COMMODITIES) {
                try {
                    int saved = fetchAndSave(mandi, commodity);
                    totalSaved += saved;
                    log.info("Saved {} records for {} / {}", saved, mandi.name(), commodity);
                } catch (Exception e) {
                    log.error("Failed to fetch {}/{}: {}", mandi.name(), commodity, e.getMessage());
                }
            }
        }

        log.info("=== MandiPriceSync complete. Total records saved: {} ===", totalSaved);
    }

    private int fetchAndSave(MandiRegistry.MandiInfo mandi, String commodity) {
        String url = String.format(
            "%s?api-key=%s&format=json&filters[state]=%s&filters[district]=%s" +
            "&filters[commodity]=%s&limit=10",
            apiBaseUrl, apiKey,
            encodeParam(mandi.state()),
            encodeParam(mandi.name()),
            encodeParam(commodity)
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("records")) return 0;

        List<Map<String, Object>> records = (List<Map<String, Object>>) response.get("records");
        int saved = 0;

        for (Map<String, Object> rec : records) {
            LocalDate priceDate = parseDate(rec.get("arrival_date").toString());

            // Skip if we already have this record
            if (mandiPriceRepository.existsByMandiNameAndCommodityAndPriceDate(
                    mandi.name(), commodity, priceDate)) {
                continue;
            }

            MandiPrice price = new MandiPrice();
            price.setMandiName(mandi.name());
            price.setState(mandi.state());
            price.setCommodity(commodity);
            price.setMinPrice(toDouble(rec.get("min_price")));
            price.setMaxPrice(toDouble(rec.get("max_price")));
            price.setModalPrice(toDouble(rec.get("modal_price")));
            price.setPriceDate(priceDate);
            price.setLatitude(mandi.latitude());
            price.setLongitude(mandi.longitude());
            // Fuel + rainfall are enriched separately by FuelPriceService
            price.setFuelPricePerLitre(MandiRegistry.DIESEL_PRICE_DEFAULT);

            mandiPriceRepository.save(price);
            saved++;
        }

        return saved;
    }

    private LocalDate parseDate(String dateStr) {
        // data.gov.in returns dates as "dd/MM/yyyy"
        String[] parts = dateStr.split("/");
        return LocalDate.of(
            Integer.parseInt(parts[2]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[0])
        );
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString().replace(",", "")); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private String encodeParam(String s) {
        return s.replace(" ", "%20");
    }
}
