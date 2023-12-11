package com.example.project3.repository;

import com.example.project3.entity.member.Member;
import com.example.project3.entity.Post;
import com.example.project3.entity.PostLiked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikedRepository extends JpaRepository<PostLiked, Long> {

    PostLiked findByPostAndMember(Post post, Member member);

    boolean existsByPostAndMember(Post post, Member member);

    List<PostLiked> findByPost_PostIdAndLiked(Long postId, boolean liked);

    List<PostLiked> findByPost_PostId(Long postId);

    void deleteByPost(Post post);
}
