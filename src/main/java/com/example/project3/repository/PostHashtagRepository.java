package com.example.project3.repository;

import com.example.project3.entity.Post;
import com.example.project3.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    List<PostHashtag> findByPost_PostIdAndHashtag_HashtagName(Long postId, String hashtagName);

    @Modifying
    @Query("DELETE FROM PostHashtag ph WHERE ph.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    void deleteByPost(Post post);
}

