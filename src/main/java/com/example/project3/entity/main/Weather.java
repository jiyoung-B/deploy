package com.example.project3.entity.main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long weatherId;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "weather_description")
    private String weatherDescription;

    @Column(name = "forecast_date")
    private Date forecastDate;

    @Column(name = "weather_condition")
    private String weatherCondition;

    @Column(name = "temperature_max")
    private Float temperatureMax;

    @Column(name = "temperature_min")
    private Float temperatureMin;

    @Column(name = "humidity")
    private Long humidity;

    @Column(name = "wind_speed")
    private Float windSpeed;

    @Column(name = "precipitation")
    private Float precipitation;

    @Column(name = "weather_img")
    private String weatherImg;

    @Column(name = "observation_time", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private Date observationTime;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "location_id") // 외래 키 설정
    private Location location;

    public String getWeatherCondition() {
        return this.weatherCondition;
    }

}
