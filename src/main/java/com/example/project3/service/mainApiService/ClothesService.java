package com.example.project3.service.mainApiService;


import com.example.project3.entity.main.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClothesService {

    @Value("${open.weather.api.key}") // application.properties에 등록한 키 값을 가져오기 위해 사용
    private String openWeatherMapApiKey;

    public String recommendOutfit(Weather weather, double temperature) {
        if (weather != null && weather.getWeatherDescription() != null) {
            String weatherDescription = weather.getWeatherDescription().toLowerCase();

            if (temperature <= 10) {
                return "따뜻한 옷";
            } else if (temperature < 15) {
                return "편한 옷";
            } else {
                // 공통 로직을 recommendOutfitForWeather 메서드로 이동
                return recommendOutfitForWeather(weatherDescription);
            }
        }

        // 기본 추천 옷
        return "기본 추천 옷";
    }

    private String recommendOutfitForWeather(String weatherDescription) {
        if (weatherDescription.contains("cloud")) {
            return "흐린 날 적당한 옷";
        } else if (weatherDescription.contains("rain")) {
            return "비 오는 날에는 우산이 필요 합니다!";
        } else if (weatherDescription.contains("sunny")) {
            return "맑은 날 적당한 옷";
        }

        // 기본 추천 옷
        return "기본 추천 옷";
    }


    public Weather getWeatherByCoordinates(double latitude, double longitude) {
        try {
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" +
                    latitude + "&lon=" + longitude + "&appid=" + openWeatherMapApiKey;

            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(apiUrl, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(" OpenWeatherMap API 로 데이터 를 가져올 수 없 습니다", e);
        }
    }
}
