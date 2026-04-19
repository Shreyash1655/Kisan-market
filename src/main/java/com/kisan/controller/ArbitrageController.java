package com.kisan.controller;
import com.kisan.service.ArbitrageService;
import com.kisan.dto.ForecastResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/arbitrage")
@CrossOrigin(origins = "*") // Allows your React Native app to connect without security blocks
public class ArbitrageController {

    @Autowired
    private ArbitrageService arbitrageService;

    @GetMapping("/test-ml")
    public ResponseEntity<ForecastResponse> getForecast(
            @RequestParam(defaultValue = "Cashew") String commodity,
            @RequestParam(defaultValue = "Panaji") String mandi) {
        
        // Pass the requested crop and location down to the Service layer
        ForecastResponse response = arbitrageService.getPrediction(commodity, mandi);
        
        return ResponseEntity.ok(response);
    }
}