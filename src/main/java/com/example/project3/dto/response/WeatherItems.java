package com.example.project3.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherItems {

    @JsonProperty("items")
    private WeatherItemList itemList;


    public WeatherItemList getItemList() {
        return itemList;
    }

}

