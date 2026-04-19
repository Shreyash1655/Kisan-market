package com.kisan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArbitrageResult {
    private String sourceMandi;
    private String targetMandi;
    private String targetState;
    private Double localPricePerKg;
    private Double targetPricePerKg;
    private Double quantityKg;
    private Double roadDistanceKm;
    private Long fuelCostRs;
    private Long tollCostRs;
    private Long totalTravelCostRs;
    private Long localRevenueRs;
    private Long targetRevenueRs;
    private Long netProfitRs;
    private Long breakEvenQuantityKg;
    private boolean isProfitable;
}