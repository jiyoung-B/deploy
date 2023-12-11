package com.example.project3.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostUpdateRequestDto {

    private String location;
    private Float temperature;
    private List<MultipartFile> newPostImages; // 새로운 멀티파트 파일 목록
    private List<String> originalImages; // 기존 이미지 URL 목록
    private String content;
    private List<String> hashtags;

}
