package com.example.project3.controller;

import com.example.project3.dto.request.PostRequestDto;
import com.example.project3.dto.request.PostUpdateRequestDto;
import com.example.project3.dto.response.MemberInfoPostResponseDto;
import com.example.project3.dto.response.PostLikedMemberResponseDto;
import com.example.project3.dto.response.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.example.project3.controller.PostController.DEFAULT_PAGE_SIZE;

@Tag(name = "게시글 관리")
public interface PostApi {
    // 게시글 등록
    @Operation(summary = "게시글 등록", description = "게시글 등록 요청, 이미지/동영상 파일은 최대 3개까지.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = "요청에 문제가 있습니다.")
    })
    ResponseEntity<String> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            PostRequestDto postRequestDto);

    @Operation(summary = "전체 게시글 목록 조회", description = "게시글 목록을 조회, 토큰 없이도 조회 가능.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    ResponseEntity<Page<PostResponseDto>> firstMainList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "" + Long.MAX_VALUE) Long lastPostId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = DEFAULT_PAGE_SIZE)
            Pageable pageable);

    // 특정 게시글 상세 조회
    @Operation(summary = "특정 게시글 상세 조회", description = "특정 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    ResponseEntity<PostResponseDto> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails);

    // 특정 게시글 수정
    @Operation(summary = "특정 게시글 수정", description = "특정 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청에 문제가 있습니다."),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            PostUpdateRequestDto postUpdateRequestDto);


    // 좋아요 등록/삭제
    @Operation(summary = "좋아요 등록/삭제", description = "특정 게시글에 좋아요를 등록/삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 등록/삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    ResponseEntity<String> toggleLike(@PathVariable Long postId,
                                      @AuthenticationPrincipal UserDetails userDetails);


    // 좋아요를 누른 유저 목록 조회
    @Operation(summary = "좋아요 누른 유저 목록 조회", description = "특정 게시글에 좋아요를 누른 유저 목록을 조회, 최대 30명까지만 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 누른 유저 목록 조회 성공")
    })
    ResponseEntity<List<PostLikedMemberResponseDto>> getLikes(@PathVariable Long postId);

    // 해시태그로 게시글 조회
    @Operation(summary = "해시태그로 게시글 조회", description = "해시태그로 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해시태그로 게시글 조회 성공")
    })
    ResponseEntity<Page<PostResponseDto>> getPostsByHashtag3(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String hashtagName,
            @RequestParam(defaultValue = "" + Long.MAX_VALUE) Long lastPostId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = DEFAULT_PAGE_SIZE)
            Pageable pageable);

    // 사용자별 게시글 조회
    @Operation(summary = "사용자별 게시글 조회", description = "사용자별 게시글을 조회합니다.\n"+
            "nickName 으로 조회.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자별 게시글 조회 성공, nickName 으로 조회")})
    ResponseEntity<MemberInfoPostResponseDto> getPostsByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickName,
            @RequestParam(defaultValue = "" + Long.MAX_VALUE) Long lastPostId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = DEFAULT_PAGE_SIZE)
            Pageable pageable);


    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    ResponseEntity<String> deletePost(@PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails);


    
}