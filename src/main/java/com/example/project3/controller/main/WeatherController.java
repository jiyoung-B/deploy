package com.example.project3.controller.main;

import com.example.project3.service.mainApiService.WeatherService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{city}/{country}")
    public String getWeather(@PathVariable String city, @PathVariable String country) {
        JSONObject weatherData = weatherService.getWeather(city, country);

        if (weatherData != null) {
            // 여기에서 필요한 날씨 정보를 선택하여 클라이언트에게 반환
            return "City: " + weatherData.getString("name") +
                    ", Temperature: " + weatherData.getJSONObject("main").getDouble("temp") +
                    ", Weather: " + weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
        } else {
            return "날씨정보를 불러오는 실패했습니다.";
        }
    }

    @GetMapping("/coordinates")
    public String getWeatherByCoordinates(@RequestParam double latitude, @RequestParam double longitude) {
        JSONObject weatherData = weatherService.getWeatherByCoordinates(latitude, longitude);
        return formatResponse(weatherData);
    }

    private String formatResponse(JSONObject weatherData) {
        if (weatherData != null) {
            return "도시: " + weatherData.getString("name") +
                    ", 온도: " + weatherData.getJSONObject("main").getDouble("temp") +
                    ", 날씨: " + weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
        } else {
            return "날씨 데이터를 가져오는 데 실패했습니다.";
        }
    }
}
