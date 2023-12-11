package com.example.project3.controller;

import com.example.project3.dto.request.PostRequestDto;
import com.example.project3.dto.request.PostUpdateRequestDto;
import com.example.project3.dto.response.MemberInfoPostResponseDto;
import com.example.project3.dto.response.PostLikedMemberResponseDto;
import com.example.project3.dto.response.PostResponseDto;
import com.example.project3.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PostController implements PostApi {

    private final PostService postService;

    public static final int DEFAULT_PAGE_SIZE = 10;

   @Override
    @PostMapping("/post")
    public ResponseEntity<String> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            PostRequestDto postRequestDto) {
        log.info("게시글 등록 요청이 들어왔습니다.");

        // 최대 3개 파일만 허용
        if (postRequestDto.getMediaFiles() != null && postRequestDto.getMediaFiles().size() > 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이미지는 최대 3장까지 등록할 수 있습니다.");
        }

        Long postId = postService.createPost(userDetails.getUsername(), postRequestDto);
        String message = "게시물이 성공적으로 등록되었습니다.";

        return ResponseEntity.status(HttpStatus.OK)
                .body(postId + message);
    }

    // 전체 게시글 목록 조회
    @Override
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDto>> firstMainList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "" + Long.MAX_VALUE) Long lastPostId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = DEFAULT_PAGE_SIZE)
            Pageable pageable) {
        log.info("게시글 전체 목록 조회 요청이 들어왔습니다.");

        if (userDetails != null) {
            // 로그인한 경우
            Page<PostResponseDto> allPostList = postService.getAllPostList(lastPostId, pageable, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(allPostList);
        } else {
            // 로그인하지 않은 경우
            Page<PostResponseDto> allPostList = postService.getAllPostList(lastPostId, pageable, null);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(allPostList);
        }

    }

    // 특정 게시글 상세 조회
    @Override
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("특정 게시글 상세 조회 요청이 들어왔습니다.");

        PostResponseDto postResponseDto = postService.getPostById(postId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(postResponseDto);
    }

    // 특정 게시글 수정
    @Override
    @PutMapping("/post/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            PostUpdateRequestDto postUpdateRequestDto) {
        log.info("특정 게시글 수정 요청이 들어왔습니다.");

        if (postUpdateRequestDto.getNewPostImages() != null && postUpdateRequestDto.getNewPostImages().size() + postUpdateRequestDto.getOriginalImages().size() > 3) {
            throw new IllegalArgumentException("사진은 3장만 등록가능합니다.");
        }
        if (postUpdateRequestDto.getNewPostImages() == null) {
            postUpdateRequestDto.setNewPostImages(Collections.emptyList());
        }



        PostResponseDto postResponseDto = postService.updatePost(postId, userDetails.getUsername(), postUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);

    }

    // 좋아요 등록/삭제
    @Override
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("좋아요 등록 or 삭제 요청이 들어왔습니다.");

        boolean isLiked = postService.toggleLike(postId, userDetails.getUsername());

        if (isLiked) {
            return ResponseEntity.status(HttpStatus.CREATED).body("좋아요가 추가되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("이미 좋아요를 누르셨습니다.");
        }

    }


    // 좋아요를 누른 유저 목록 조회
    @Override
    @GetMapping("/post/{postId}/likers")
    public ResponseEntity<List<PostLikedMemberResponseDto>> getLikes(@PathVariable Long postId) {

        List<PostLikedMemberResponseDto> likedUsers = postService.getLikers(postId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(likedUsers);
    }


    // 해시태그로 게시글 조회
    @Override
    @GetMapping("/posts/hashtag/{hashtagName}")
    public ResponseEntity<Page<PostResponseDto>> getPostsByHashtag3(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String hashtagName,
            @RequestParam(defaultValue = "" + Long.MAX_VALUE) Long lastPostId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = DEFAULT_PAGE_SIZE)
            Pageable pageable) {
        log.info("특정 해시태그가 포함된 게시글 목록 조회 요청이 들어왔습니다.");

        String userEmail = (userDetails != null) ? userDetails.getUsername() : null;

        Page<PostResponseDto> postsByHashtag = postService.getPostsByHashtag(hashtagName, lastPostId, pageable, userEmail);

        return ResponseEntity.status(HttpStatus.OK)
                .body(postsByHashtag);
    }

    // 사용자별 게시글 조회
    @Override
    @GetMapping("/posts/user/{nickName}")
    public ResponseEntity<MemberInfoPostResponseDto> getPostsByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String nickName,
            @RequestParam(defaultValue = "" + Long.MAX_VALUE) Long lastPostId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = DEFAULT_PAGE_SIZE)
            Pageable pageable) {
        log.info("사용자별 게시글 조회 요청이 들어왔습니다.");

        String loggedInUserEmail = (userDetails != null) ? userDetails.getUsername() : null;

        Page<PostResponseDto> postsByUser = postService.getPostsByUser(nickName, lastPostId, pageable, loggedInUserEmail);

        MemberInfoPostResponseDto memberInfo = postService.getMemberInfo(nickName);


        return ResponseEntity.status(HttpStatus.OK)
                .body(MemberInfoPostResponseDto.builder()
                        .memberId(memberInfo.getMemberId())
                        .userName(memberInfo.getUserName())
                        .email(memberInfo.getEmail())
                        .nickName(memberInfo.getNickName())
                        .imageUrl(memberInfo.getImageUrl())
                        .postResponseDtos(postsByUser.getContent())
                        .pageable(postsByUser.getPageable())  // 페이징 정보 추가
                        .last(postsByUser.isLast())
                        .totalElements(postsByUser.getTotalElements())
                        .totalPages(postsByUser.getTotalPages())
                        .size(postsByUser.getSize())
                        .number(postsByUser.getNumber())
                        .first(postsByUser.isFirst())
                        .numberOfElements(postsByUser.getNumberOfElements())
                        .empty(postsByUser.isEmpty())
                        .build());
    }

    // 게시글 삭제
    @Override
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("게시글 삭제 요청이 들어왔습니다.");

        try {
            Long deletedPostId = postService.deletePost(postId, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(deletedPostId + " 게시글이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }



}