package com.kisan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataPoint {
    private String date;
    private Double modal_price;
    private Double fuel_price;
    private Double rainfall_mm;
}