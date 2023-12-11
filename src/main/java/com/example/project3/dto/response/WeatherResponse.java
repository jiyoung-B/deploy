package com.example.project3.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResponse {

    private Main main;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("temperature")
    private Float temperature;

    @JsonProperty("isRaining")
    private boolean isRaining;

    @JsonProperty("weatherCondition")
    private String weatherCondition;

    @JsonProperty("locationName")
    private String locationName;

    @JsonProperty("minTemp")
    private double minTemp;

    @JsonProperty("maxTemp")
    private double maxTemp;

    @JsonProperty("precipitation")
    private double precipitation;

    @JsonProperty("uvIndex")
    private double uvIndex;

    @Getter
    @Setter
    public static class Main {
        @JsonProperty("temperature")
        private float temperature;
    }
}
