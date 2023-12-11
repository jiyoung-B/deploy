package com.example.project3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PostLikedMemberResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String imageUrl;
    private String nickName;
}