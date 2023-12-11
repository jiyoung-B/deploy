package com.example.project3.repository;

import com.example.project3.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Hashtag findHashtagByHashtagName(String hashtagName);

    Hashtag findByHashtagName(String newHashtag);
}
