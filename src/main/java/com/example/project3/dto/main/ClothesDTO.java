package com.example.project3.dto.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClothesDTO {
    private Long clothesId;
    private String top;
    private String bottom;
    private Long weatherId;
    private Long memberId;
    private String clothesImg;
}
