package com.example.project3.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherItem {

    @JsonProperty("regId")
    private String regId;

    @JsonProperty("taMin3")
    private int taMin3;

    @JsonProperty("taMax3")
    private int taMax3;

    @JsonProperty("taMin4")
    private int taMin4;

    @JsonProperty("taMax4")
    private int taMax4;

    @JsonProperty("taMin5")
    private int taMin5;

    @JsonProperty("taMax5")
    private int taMax5;

    @JsonProperty("taMin6")
    private int taMin6;

    @JsonProperty("taMax6")
    private int taMax6;

    @JsonProperty("taMin7")
    private int taMin7;

    @JsonProperty("taMax7")
    private int taMax7;

    @JsonProperty("taMin8")
    private int taMin8;

    @JsonProperty("taMax8")
    private int taMax8;

    @JsonProperty("taMin9")
    private int taMin9;

    @JsonProperty("taMax9")
    private int taMax9;

    @JsonProperty("taMin10")
    private int taMin10;

    @JsonProperty("taMax10")
    private int taMax10;

    public String getRegId() {
        return regId;
    }



    public int getTaMin(int day) {
        switch (day) {
            case 3: return taMin3;
            case 4: return taMin4;
            case 5: return taMin5;
            case 6: return taMin6;
            case 7: return taMin7;
            case 8: return taMin8;
            case 9: return taMin9;
            case 10: return taMin10;
            default: throw new IllegalArgumentException("잘못된 날짜: " + day);
        }
    }

    public int getTaMax(int day) {
        switch (day) {
            case 3: return taMax3;
            case 4: return taMax4;
            case 5: return taMax5;
            case 6: return taMax6;
            case 7: return taMax7;
            case 8: return taMax8;
            case 9: return taMax9;
            case 10: return taMax10;
            default: throw new IllegalArgumentException("잘못된 날짜: " + day);
        }
    }
}

