package com.kisan.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data // This automatically generates the setMinPrice and setMaxPrice methods
public class MandiPrice {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String mandiName;
    private String state;
    private String commodity;
    
    // ✅ ADDED THESE TWO FIELDS:
    private Double minPrice;
    private Double maxPrice;
    
    private Double modalPrice;
    private LocalDate priceDate;
    private Double fuelPricePerLitre;
    private Double latitude;
    private Double longitude;
}