package com.kisan.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ForecastRequest {
    private String mandi_name;
    private String commodity;
    private int forecast_days;
    private List<DataPoint> history;
}