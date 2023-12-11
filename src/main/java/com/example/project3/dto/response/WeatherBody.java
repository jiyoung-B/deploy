package com.example.project3.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherBody {

    private String dataType;

    @JsonProperty("body")
    private WeatherItems items;

    private int numOfRows;
    private int pageNo;
    private int totalCount;

}