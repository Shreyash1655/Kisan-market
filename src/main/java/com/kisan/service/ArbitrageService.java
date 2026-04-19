package com.kisan.service; // ✅ Updated package name

import com.kisan.dto.ForecastResponse; // ✅ You will need to import your DTO
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ArbitrageService {

    private final RestTemplate restTemplate;

    public ArbitrageService() {
        this.restTemplate = new RestTemplate();
    }

    public ForecastResponse getPrediction(String commodity, String mandi) {
        String pythonBaseUrl = "http://localhost:8000/api/arbitrage/test-ml";

        String url = UriComponentsBuilder.fromHttpUrl(pythonBaseUrl)
                .queryParam("commodity", commodity)
                .queryParam("mandi", mandi)
                .toUriString();

        try {
            return restTemplate.getForObject(url, ForecastResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("AI Brain communication failed: " + e.getMessage());
        }
    }
}