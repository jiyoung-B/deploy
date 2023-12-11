package com.example.project3.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Schema(name = "회원 정보 수정 요청 정보")
public class UpdateUserInfoRequest {

    @Schema(description = "회원 주소" , example = "서울특별시")
    private String address;

    @Schema(description = "회원 별명" , example = "별명1")
    private String nickName;

    @Schema(description = "회원 한 줄 메시지" , example = "날씨가 좋습니다.")
    private String message;
}
