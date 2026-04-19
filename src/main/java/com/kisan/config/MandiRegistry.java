package com.kisan.config;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MandiRegistry {
    public record MandiInfo(String name, String state, double latitude, double longitude, double tollFromGoa) {}

    public static final double DIESEL_PRICE_DEFAULT = 90.0;
    public static final double TRUCK_LITRES_PER_KM = 0.15; // Approx 6.5 km/l

    public static final List<MandiInfo> ALL_MANDIS = List.of(
        new MandiInfo("Panaji", "Goa", 15.4909, 73.8278, 0),
        new MandiInfo("Belgaum", "Karnataka", 15.8497, 74.4977, 370.0),
        new MandiInfo("Sindhudurg", "Maharashtra", 16.1114, 73.6934, 150.0)
    );
}