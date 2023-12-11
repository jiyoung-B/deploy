package com.example.project3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberInfoPostResponseDto {

    private Long memberId;
    private String userName;
    private String email;
    private String nickName;
    private String imageUrl;
    private List<PostResponseDto> postResponseDtos;
    // 페이징 정보 추가
    private Pageable pageable;
    private boolean last;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private boolean first;
    private int numberOfElements;
    private boolean empty;
}