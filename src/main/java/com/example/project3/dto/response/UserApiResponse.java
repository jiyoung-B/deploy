package com.example.project3.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApiResponse {

    private Long userId;
    private LocationResponse location; // 외부 API에서 제공하는 위치 정보 응답 형식

}
