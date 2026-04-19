package com.kisan.service;

import com.kisan.config.MandiRegistry;
import com.kisan.dto.ArbitrageResult;
import org.springframework.stereotype.Service;

/**
 * Haversine formula to calculate great-circle distance between two GPS points.
 *
 * Profit formula:
 *   Profit = (P_target × Q) - (P_local × Q) - (Dist × F_rate) - T_toll
 *
 * Where:
 *   P_target   = Predicted price at target mandi (₹/kg)
 *   P_local    = Current price at local (Goa) mandi (₹/kg)
 *   Q          = Quantity in kg
 *   Dist       = Road distance (approx. 1.3× straight-line Haversine)
 *   F_rate     = Fuel cost per km (truck litres/km × diesel price)
 *   T_toll     = Inter-state toll fees
 */
@Service
public class ArbitrageCalculatorService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double ROAD_FACTOR = 1.3; // Haversine → road distance multiplier

    /**
     * Calculate straight-line distance in km using the Haversine formula.
     */
    public double haversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1))
                 * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimated road distance = Haversine × 1.3 correction factor.
     */
    public double roadDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        return haversineDistanceKm(lat1, lon1, lat2, lon2) * ROAD_FACTOR;
    }

    /**
     * Full arbitrage profit calculation.
     *
     * @param localPricePerKg    Current modal price in Goa mandi (₹/kg)
     * @param targetPricePerKg   Predicted price at target mandi (₹/kg)
     * @param quantityKg         Farmer's harvest quantity
     * @param sourceMandi        Goa mandi info
     * @param targetMandi        Destination mandi info
     * @param dieselPricePerLitre Current diesel price
     * @return ArbitrageResult with full cost breakdown
     */
    public ArbitrageResult calculate(
        double localPricePerKg,
        double targetPricePerKg,
        double quantityKg,
        MandiRegistry.MandiInfo sourceMandi,
        MandiRegistry.MandiInfo targetMandi,
        double dieselPricePerLitre
    ) {
        double roadKm = roadDistanceKm(
            sourceMandi.latitude(), sourceMandi.longitude(),
            targetMandi.latitude(), targetMandi.longitude()
        );

        // Fuel cost = distance × (litres/km) × (price/litre)  [one-way × 2 for return trip]
        double fuelCostOneWay = roadKm * MandiRegistry.TRUCK_LITRES_PER_KM * dieselPricePerLitre;
        double totalFuelCost  = fuelCostOneWay * 2; // return empty

        double tollCost       = targetMandi.tollFromGoa() * 2; // both directions

        double localRevenue   = localPricePerKg  * quantityKg;
        double targetRevenue  = targetPricePerKg * quantityKg;

        double priceDifference = targetRevenue - localRevenue;
        double travelCost      = totalFuelCost + tollCost;
        double netProfit       = priceDifference - travelCost;

        double breakEvenQuantityKg = (travelCost > 0 && targetPricePerKg > localPricePerKg)
            ? travelCost / (targetPricePerKg - localPricePerKg)
            : Double.MAX_VALUE;

        return ArbitrageResult.builder()
            .sourceMandi(sourceMandi.name())
            .targetMandi(targetMandi.name())
            .targetState(targetMandi.state())
            .localPricePerKg(localPricePerKg)
            .targetPricePerKg(targetPricePerKg)
            .quantityKg(quantityKg)
            .roadDistanceKm(Math.round(roadKm * 10.0) / 10.0)
            .fuelCostRs(Math.round(totalFuelCost))
            .tollCostRs(Math.round(tollCost))
            .totalTravelCostRs(Math.round(travelCost))
            .localRevenueRs(Math.round(localRevenue))
            .targetRevenueRs(Math.round(targetRevenue))
            .netProfitRs(Math.round(netProfit))
            .breakEvenQuantityKg(Math.round(breakEvenQuantityKg))
            .isProfitable(netProfit > 0)
            .build();
    }
}
