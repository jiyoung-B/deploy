package com.example.project3.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @Column(name = "file_url")
    private String fileUrl;

    public MediaFile(String fileUrl, Post post) {
        this.fileUrl = fileUrl;
        this.post = post;
    }

    public MediaFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public void setPost2(Post post) {
        this.post = post;
        post.getMediaFiles().add(this);
    }
}
