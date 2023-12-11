package com.example.project3.controller.main;


import com.example.project3.entity.main.Weather;
import com.example.project3.service.mainApiService.ClothesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clothes")
public class ClothesController {

    private final ClothesService clothesService;

    @Autowired
    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @GetMapping("/recommend")
    public String recommendOutfit() {
        try {
            // 프론트엔드에서 받아온 위치 정보
            double latitude = 37.7749; // 예시 위도
            double longitude = -122.4194; // 예시 경도

            // 프론트엔드에서 받아온 날씨 정보 (WeatherInfo 컴포넌트에서 받아온 형태에 따라 수정 필요)
            Weather weather = new Weather();
            weather.setWeatherDescription("Clear"); // 예시 날씨

            // 프론트엔드에서 받아온 온도 정보 (WeatherInfo 컴포넌트에서 받아온 형태에 따라 수정 필요)
            double temperature = 25.0; // 예시 온도

            // ClothesService를 통해 날씨에 따른 옷 추천을 받아옴
            String outfitRecommendation = clothesService.recommendOutfit(weather, temperature);



            return outfitRecommendation;
        } catch (Exception e) {
            e.printStackTrace();
            return "에러 발생: " + e.getMessage();
        }
    }
}
