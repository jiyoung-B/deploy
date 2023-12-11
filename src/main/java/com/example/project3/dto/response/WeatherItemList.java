package com.example.project3.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherItemList {

    @JsonProperty("item")
    private WeatherItem[] item;

}