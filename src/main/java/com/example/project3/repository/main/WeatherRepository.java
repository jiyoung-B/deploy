package com.example.project3.repository.main;


import com.example.project3.entity.main.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    // 특정 위치의 가장 최근 날씨 정보 조회
    Optional<Weather> findTopByLocation_LocationIdOrderByObservationTimeDesc(Long locationId);


    // 경도와 위도를 기반으로 날씨 조회
    List<Weather> findByLatitudeAndLongitude(double latitude, double longitude);

    List<Weather> findByForecastDate(Date date);


}
