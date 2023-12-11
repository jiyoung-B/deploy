package com.example.project3.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostRequestDto {
    private String location;
    private Float temperature;
    private List<MultipartFile> mediaFiles;
    private String content;
    private List<String> hashtags;
}
