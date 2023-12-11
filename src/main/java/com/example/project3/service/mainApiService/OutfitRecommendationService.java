package com.example.project3.service.mainApiService;


import com.example.project3.repository.main.ClothesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class OutfitRecommendationService {

    private final ClothesRepository clothesRepository;

    @Autowired
    public OutfitRecommendationService(ClothesRepository clothesRepository) {
        this.clothesRepository = clothesRepository;
    }

    // 날씨에 따라 옷 추천하기
    public String recommendClothesByWeather(String weather) {
        // 날씨에 따라 추천할 옷을 데이터베이스에서 조회하거나 로직을 구현
        // 이 예제에서는 간단하게 날씨에 따라 다른 문구를 반환
        switch (weather.toLowerCase()) {
            case "맑음":
                return "맑은 날씨 입니다.";
            case "비":
                return "비오는 날 입니다";
            case "눈":
                return "눈 오는 날 입니다.";
            default:
                return "날씨 정보 가 없습 니다.";
        }
    }
}

