package com.example.project3.dto.response;

import com.example.project3.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Schema(name = "회원 정보 조회 시 List로 응답되는 등록했던 글 응답")
@Getter
@AllArgsConstructor
public class SimplifiedPostResponse {

    @Schema(description = "글ID", example = "1")
    private Long postId;

    @Schema(description = "글 등록 날짜", example = "2023-11-28 15:14:41")
    private LocalDateTime createdAt;

    @Schema(description = "등록한 글당 하나의 이미지만 응답")
    private List<String> mediaUrls;

    public SimplifiedPostResponse(Post post) {
        this.postId = post.getPostId();
        this.createdAt = post.getCreatedAt();
        // MediaFile이 존재하는 경우에만 첫 번째 MediaFile의 fileUrl을 추가
        this.mediaUrls = post.getMediaFiles().isEmpty()
                            ? Collections.emptyList()
                            : Collections.singletonList(post.getMediaFiles().get(0).getFileUrl());
    }
}