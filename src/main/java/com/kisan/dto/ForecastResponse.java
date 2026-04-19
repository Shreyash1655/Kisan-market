package com.kisan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ForecastResponse {

    private String commodity;
    
    @JsonProperty("mandi_name")
    private String mandiName;
    
    @JsonProperty("current_price")
    private Double currentPrice;
    
    @JsonProperty("confidence_score")
    private Double confidenceScore;
    
    private List<PredictionData> predictions;

    // --- Inner Class for the daily predictions ---
    public static class PredictionData {
        private String date;
        
        @JsonProperty("modal_price")
        private Double modalPrice;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Double getModalPrice() { return modalPrice; }
        public void setModalPrice(Double modalPrice) { this.modalPrice = modalPrice; }
    }

    // --- Getters and Setters ---
    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public String getMandiName() { return mandiName; }
    public void setMandiName(String mandiName) { this.mandiName = mandiName; }

    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public List<PredictionData> getPredictions() { return predictions; }
    public void setPredictions(List<PredictionData> predictions) { this.predictions = predictions; }
}