package com.example.project3.repository;

import com.example.project3.entity.MediaFile;
import com.example.project3.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    @Modifying
    @Query("delete from MediaFile mf " +
            "where mf.post.postId = :postId " +
            "and mf.fileUrl = :fileUrl")
    void deleteByPostIdAndFileUrl(@Param("postId") Long postId, @Param("fileUrl") String fileUrl);

    void deleteByPost(Post post);
}