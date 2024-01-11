package com.example.project3.entity;

import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
//@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;
    @Column(unique = true)
    private String hashtagName;

    @Builder.Default
    @OneToMany(mappedBy = "hashtag")
    private List<PostHashtag> postHashtags = new ArrayList<>();

    public Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    public void addPostHashtag(PostHashtag postHashtag) {
        if (this.postHashtags == null) {
            this.postHashtags = new ArrayList<>();
        }
        this.postHashtags.add(postHashtag);
        postHashtag.setHashtag(this);
    }
}
