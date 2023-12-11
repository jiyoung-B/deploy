package com.example.project3.service;

import com.example.project3.entity.*;
import com.example.project3.entity.member.Member;
import com.example.project3.dto.request.PostRequestDto;
import com.example.project3.dto.request.PostUpdateRequestDto;
import com.example.project3.dto.response.MemberInfoPostResponseDto;
import com.example.project3.dto.response.PostLikedMemberResponseDto;
import com.example.project3.dto.response.PostResponseDto;
import com.example.project3.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikedRepository postLikedRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final MediaFileRepository mediaFileRepository;
    private final HashtagRepository hashtagRepository;
    private final S3Uploader s3Uploader;


    @Transactional
    public Long createPost(String username, PostRequestDto requestDto) {

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(()->new IllegalArgumentException("가입된 정보가 없는 이메일"));

        Post post = Post.builder()
                .postLocation(requestDto.getLocation())
                .postContent(requestDto.getContent())
                .postTemperature(requestDto.getTemperature())
                .member(member)
                .build();

        // DB에 저장
        Post savedPost = postRepository.save(post);

        // MediaFiles 처리
        saveMediaFiles(requestDto.getMediaFiles(), post);
        log.info("미디어 처리 완료.");
        // Hashtag 처리
        saveHashtagNames(requestDto.getHashtags(), post);
        //Post savedPost = postRepository.save(post);

        return savedPost.getPostId();

    }

    private void saveMediaFiles(List<MultipartFile> mediaFiles, Post post) {
        log.info("사진 저장 로직 실행중");
        if (mediaFiles == null) {
            Collections.emptyList();
        }

        List<String> mediaUrls = s3Uploader.upload(mediaFiles); // 수정

        log.info("S3 업로드 후 url 반환 = {}", mediaUrls);
        // 각 URL을 Post 엔터티에 추가
        if (post.getMediaFiles() == null) {
            post.setMediaFiles();
        }

        // 각 URL을 Post 엔티티에 추가하고 MediaFile 객체를 리스트에 추가
        for (String mediaUrl : mediaUrls) {
            MediaFile mediaFile = new MediaFile(mediaUrl, post);
            post.addMediaFile(mediaFile);
        }


    }

    private void saveHashtagNames(List<String> hashtagNames, Post post) {

        // 기존 해시태그가 null이 아닌 경우에 clear
        if (post.getPostHashtags() != null) {
            post.getPostHashtags().clear();
        } else {
            post.setPostHashtags(new ArrayList<>()); // null이면 새로운 리스트 생성
        }

        for (String hashtagName : hashtagNames) {
            // 데이터베이스에 해시태그가 이미 존재하는지 확인
            Hashtag existingHashtag = hashtagRepository.findHashtagByHashtagName(hashtagName);

            // 존재하지 않으면 생성 및 저장
            if (existingHashtag == null) {
                existingHashtag = new Hashtag(hashtagName);
                existingHashtag = hashtagRepository.save(existingHashtag);
            }

            // PostHashtag 생성 및 저장
            PostHashtag postHashtag = new PostHashtag(post, existingHashtag);
            existingHashtag.addPostHashtag(postHashtag);

            // 저장된 해시태그의 이름을 리스트에 추가
            post.getPostHashtags().add(postHashtag);
        }

    }



    @Transactional
    public boolean toggleLike(Long postId, String userEmail) {
        if (userEmail == null) {
            // 사용자가 로그인되지 않았습니다. false를 반환합니다.
            return false;
        }

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        PostLiked postLiked = postLikedRepository.findByPostAndMember(post, member);

        if (postLiked != null) {
            // 이미 좋아요를 눌렀으면 취소
            postLikedRepository.delete(postLiked);
            post.decreaseCountLiked();
            return false;
        } else {
            // 좋아요를 누르지 않았으면 좋아요 추가
            postLikedRepository.save(PostLiked.builder().post(post).member(member).liked(true).build());
            post.increaseCountLiked();
            return true;
        }
    }

    public Page<PostResponseDto> getAllPostList(Long lastPostId, Pageable pageable, String userEmail) {
        // 게시글을 페이징하여 가져오기
        Page<Post> posts = postRepository.findByPostIdLessThanOrderByCreatedAtDesc(lastPostId, pageable);

        // Page<Post>를 Page<PostResponseDto>로 변환
        Page<PostResponseDto> postResponseDtoPage = posts.map(post -> createPostResponseDto(post, userEmail));

        return postResponseDtoPage;
    }

    private PostResponseDto createPostResponseDto(Post post, String userEmail) {
        Member member = (userEmail != null)
                ? memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail))
                : post.getMember(); // 사용자가 로그인하지 않은 경우 게시글 작성자 정보 사용


        List<String> mediaUrls = post.getMediaFiles().stream()
                .map(MediaFile::getFileUrl)
                .collect(Collectors.toList());

        boolean isPostLiked = userEmail != null && postLikedRepository.existsByPostAndMember(post, member); // 사용자가 로그인하지 않은 경우 좋아요 여부 false로 설정

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return PostResponseDto.builder()
                .postId(post.getPostId())
                .userId(post.getMember().getId())
                .userImg(post.getMember().getImageURL())
                .userName(post.getMember().getName())
                .userEmail(post.getMember().getEmail())
                .nickName(post.getMember().getNickName())
                .date(post.getCreatedAt().format(formatter))
                .location(post.getPostLocation())
                .temperature(post.getPostTemperature())
                .mediaUrls(mediaUrls)
                .content(post.getPostContent())
                .liked(isPostLiked)
                .likedCount(post.getCountLiked())
                .hashtagNames(post.getPostHashtags().stream()
                        .map(PostHashtag::getHashtag)
                        .map(Hashtag::getHashtagName)
                        .distinct()
                        .collect(Collectors.toList()))
                .build();
    }


    public PostResponseDto getPostById(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        return createPostResponseDto(post, userEmail);
    }




    @Transactional
    public PostResponseDto updatePost(Long postId, String username, PostUpdateRequestDto request) {

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        post.update(request);

        // 기존 이미지와 넘어온 이미지 비교
        List<String> updateOriginalImages = request.getOriginalImages();
        List<String> postImages = getExistingImageUrls(post.getMediaFiles());
        // 원래 있던 이미지에서 빠진 이미지를 찾아냄
        //List<String> removeImages = pickUpRemovePostImages(postImages, updateOriginalImages);
        List<String> removeImages = (postImages != null)
                ? pickUpRemovePostImages(postImages, updateOriginalImages)
                : Collections.emptyList();

        // 레파지토리에서 이미지 삭제, S3에서 빠진 이미지 파일 삭제
        if (!removeImages.isEmpty()) {
            for (String deletedImage : removeImages) {
                mediaFileRepository.deleteByPostIdAndFileUrl(postId, deletedImage);
                s3Uploader.delete(deletedImage);
            }
        }

        // 새로운 이미지 파일 추가
        addPostImages(post, request.getNewPostImages());

        updatePostHashtags(post, request.getHashtags());

        // 수정된 게시글 저장
        postRepository.save(post);


        // 수정된 게시글의 응답 DTO 생성
        return createPostResponseDto(post, username);
    }

    private List<String> getExistingImageUrls(List<MediaFile> existingImages) {
        return existingImages.stream()
                .map(MediaFile::getFileUrl)
                .collect(Collectors.toList());
    }
    private List<String> pickUpRemovePostImages(List<String> originImage, List<String> updateImage) {
        return originImage.stream()
                .filter(image -> !updateImage.contains(image))
                .collect(Collectors.toList());
    }

    private void addPostImages(Post post, List<MultipartFile> mediaFiles) {
        List<String> postMediaUrls = s3UploadAndConverter(mediaFiles);
        // 기존 이미지 파일과 새로 추가된 이미지 파일의 중복을 방지하기 위해 새로운 이미지 추가 전에 모든 기존 이미지를 삭제
        post.getMediaFiles().clear();


        for (String mediaUrl : postMediaUrls) {
            MediaFile mediaFile = new MediaFile(mediaUrl);
            //post.addPostImage(mediaFile);
            post.addMediaFile(mediaFile); // addMediaFile 메서드로 추가하도록 수정
        }
    }
    public List<String> s3UploadAndConverter(List<MultipartFile> multipartFiles) {
        List<String> mediaUrls = s3Uploader.upload(multipartFiles);
        return mediaUrls;
    }
    private void updatePostHashtags(Post post, List<String> newHashtags) {
        // 기존의 해시태그를 가져옵니다.
        List<PostHashtag> existingPostHashtags = post.getPostHashtags();

        // 기존 해시태그를 삭제합니다.
        existingPostHashtags.clear();

        // 기존 해시태그 삭제
        postHashtagRepository.deleteByPostId(post.getPostId());
        log.info("해시태그 삭제 By postId");

        // 새로운 해시태그를 추가합니다.
        for (String newHashtag : newHashtags) {
            // 데이터베이스에 해시태그가 이미 존재하는지 확인
            Hashtag existingKeyword = hashtagRepository.findByHashtagName(newHashtag);

            // 존재하지 않으면 생성 및 저장
            if (existingKeyword == null) {
                existingKeyword = new Hashtag(newHashtag);
                existingKeyword = hashtagRepository.save(existingKeyword);
            }

            // PostHashtag 생성 및 저장
            PostHashtag postHashtag = new PostHashtag(post, existingKeyword);
            postHashtagRepository.save(postHashtag);
            existingPostHashtags.add(postHashtag);
        }
    }
    public List<PostLikedMemberResponseDto> getLikers(Long postId) {
        // 특정 postId에 대한 PostLiked 정보 가져오기
        List<PostLiked> postLikedList = postLikedRepository.findByPost_PostId(postId);

        // 결과를 저장할 리스트 초기화
        List<PostLikedMemberResponseDto> responseDtoList = new ArrayList<>();

        int count = 0;

        // 각 PostLiked 정보에 대해
        for (PostLiked postLiked : postLikedList) {
            // liked가 true인 경우에만 처리
            if (postLiked.isLiked()) {
                // 해당 Member 정보 가져오기
                Member member = postLiked.getMember();
                // Member 정보를 MemberResponseDto로 변환하여 결과 리스트에 추가
                PostLikedMemberResponseDto responseDto = PostLikedMemberResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .imageUrl(member.getImageURL())
                        .nickName(member.getNickName())
                        .build();
                responseDtoList.add(responseDto);

                // 30명까지만 추가하도록 수정
                count++;
                if (count >= 30) {
                    break;
                }
            }
        }

        return responseDtoList;
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByHashtag(String hashtag, Long lastPostId, Pageable pageable, String userEmail) {
        // 특정 해시태그를 포함하는 게시글을 페이징하여 가져오기
        Page<Post> posts = postRepository.findByPostHashtags_Hashtag_HashtagNameAndPostIdLessThanOrderByCreatedAtDesc(hashtag, lastPostId, pageable);
        //Page<Post> posts = postRepository.findByHashtagAndPostIdLessThanOrderByCreatedAtDesc(hashtag, lastPostId, pageable);

        // Page<Post>를 Page<PostResponseDto>로 변환
        Page<PostResponseDto> postResponseDtoPage = posts.map(post -> createPostResponseDto(post, userEmail));

        return postResponseDtoPage;
    }

    public Page<PostResponseDto> getPostsByUser(String nickName, Long lastPostId, Pageable pageable, String loggedInUserEmail) {
        log.info("찾을유저={}", nickName);
        // 특정 유저가 작성한 게시글을 페이징하여 가져오기
        Page<Post> posts = postRepository.findByMember_NickNameAndPostIdLessThanOrderByCreatedAtDesc(nickName, lastPostId, pageable);

        // Page<Post>를 Page<PostResponseDto>로 변환
        Page<PostResponseDto> postResponseDtoPage = posts.map(post -> createPostResponseDto(post, loggedInUserEmail));

        return postResponseDtoPage;
    }

    public MemberInfoPostResponseDto getMemberInfo(String nickName) {
        Member member = memberRepository.findByNickName(nickName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with nickName: " + nickName));

        // MemberInfoPostResponseDto를 만들어서 반환
        return MemberInfoPostResponseDto.builder()
                .memberId(member.getId())
                .userName(member.getName())
                .nickName(member.getNickName())
                .email(member.getEmail())
                .imageUrl(member.getImageURL())
                .build();
    }

    @Transactional
    public Long deletePost(Long postId, String userEmail) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        if (!post.getMember().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("해당 게시글을 삭제할 권한이 없습니다.");
        }
        // 게시글에 연관된 좋아요 정보 삭제
        postLikedRepository.deleteByPost(post);

        // 게시글과 연관된 미디어 파일 삭제
        List<MediaFile> mediaFiles = post.getMediaFiles();
        for (MediaFile mediaFile : mediaFiles) {
            // S3에서 파일 삭제
            s3Uploader.delete(mediaFile.getFileUrl());
        }

        //mediaFileRepository.deleteByPost(post);

        // 게시글과 연관된 해시태그 삭제
        //postHashtagRepository.deleteByPost(post);

        // 게시글 삭제
        postRepository.deleteById(postId);

        return postId;
    }


}