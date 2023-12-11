package com.example.project3.entity;

import com.example.project3.entity.member.Member;
import com.example.project3.dto.request.PostUpdateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long postId;
    private String postContent;
    private String postLocation;
    private Float postTemperature;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<MediaFile> mediaFiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostHashtag> postHashtags = new ArrayList<>();

    private LocalDateTime createdAt;


    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PostLiked> postLikeds = new ArrayList<>();



    private int countLiked; // 좋아요 수
//    public int getCountLiked() {
//        return postLikeds.size();
//    }


    public void setPostHashtags(List<PostHashtag> postHashtags) {
    this.postHashtags = postHashtags;
}

    // 좋아요 수 증가
    public void increaseCountLiked() {
        countLiked++;
    }
//PostLiked 엔터티를 생성하여 postLikeds 리스트에 추가
//    public void increaseCountLiked(Member member) {
//        PostLiked postLiked = PostLiked.builder().post(this).member(member).liked(true).build();
//        postLikeds.add(postLiked);
//    }
    // 좋아요 수 감소
    public void decreaseCountLiked() {
        countLiked = Math.max(countLiked - 1, 0);
    }
// PostLiked 엔터티를 생성하고 해당 엔터티와 일치하는 것을 postLikeds 리스트에서 제거
//    public void decreaseCountLiked(Member member) {
//        PostLiked postLiked = PostLiked.builder().post(this).member(member).liked(false).build();
//        postLikeds.remove(postLiked);
//    }

    @PrePersist // 디비에 INSERT 되기 직전에 실행
    public void createAt() {
        this.createdAt = LocalDateTime.now();
    }

    public void setMediaFiles() {
        this.mediaFiles = new ArrayList<>();
    }

    public void addMediaFile(MediaFile mediaFile) {
        this.mediaFiles.add(mediaFile);
        mediaFile.setPost(this);
        //mediaFile.setPost(mediaFile.getPost());
        //mediaFiles.add(mediaFile);
    }

    public void update(PostUpdateRequestDto requestDto) {
        this.postLocation = requestDto.getLocation();
        this.postTemperature = requestDto.getTemperature();
        this.postContent = requestDto.getContent();
    }

}
