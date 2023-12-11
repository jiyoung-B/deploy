package com.example.project3.dto.main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class WeatherDTO {

    private Long weatherId;

    private double temperature;

    private String weather_description;

    private Date forecast_date;

    private String weather_condition;

    private double temperature_max;

    private double temperature_min;

    private int humidity;

    private double wind_speed;

    private double precipitation;

    private String weather_img;

    private LocalDateTime observation_time;

    private int location_id;


}
