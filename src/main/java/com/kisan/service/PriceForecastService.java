package com.kisan.service;

import com.kisan.dto.*;
import com.kisan.entity.MandiPrice;
import com.kisan.repository.MandiPriceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceForecastService {

    private final RestTemplate restTemplate;
    private final MandiPriceRepository repository;

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public PriceForecastService(RestTemplate restTemplate, MandiPriceRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public ForecastResponse getPrediction(String mandi, String commodity) {
        // Fetch historical data from PostgreSQL
        List<MandiPrice> historyEntities = repository.findAll(); 

        // Convert Database rows into Python-friendly DataPoints
        List<DataPoint> history = historyEntities.stream()
            .map(e -> new DataPoint(
                    e.getPriceDate().toString(), 
                    e.getModalPrice(), 
                    e.getFuelPricePerLitre() != null ? e.getFuelPricePerLitre() : 90.0, 
                    0.0)) // Defaulting rainfall to 0 for now
            .collect(Collectors.toList());

        // Build the package to send
        ForecastRequest request = ForecastRequest.builder()
                .mandi_name(mandi)
                .commodity(commodity)
                .forecast_days(7)
                .history(history)
                .build();

        // Send POST request to FastAPI
        return restTemplate.postForObject(mlServiceUrl + "/predict", request, ForecastResponse.class);
    }
}