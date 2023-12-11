package com.example.project3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostResponseDto {
    private Long postId;
    private Long userId;
    private String userImg;
    private String userEmail;
    private String userName;
    private String nickName;
    private String date;
    private String location;
    private Float temperature;
    private List<String> mediaUrls;
    private String content;
    private Boolean liked;
    private int likedCount;
    private List<String> hashtagNames;


}