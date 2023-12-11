package com.example.project3.entity.member;

import com.example.project3.entity.Post;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
@AllArgsConstructor
@Slf4j
public class Member{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private String imageURL;

    @Column(unique = true)
    private String nickName;

    private String message;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private String refreshToken;

    @Builder.Default
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();


    // 자체 회원가입용 빌더
    @Builder
    public Member (String name, String email, String password, String address,
                  String imageURL, String nickName, String message, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.imageURL = imageURL;
        this.nickName = nickName;
        this.message = message;
        this.role = role;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }


    public void signupSocialUser(String message, String address, String nickName) {
        log.info("signupSocialUser() 실행");
        log.info("message : {}", message);
        log.info("address : {}", address);
        log.info("nickName : {}", nickName);
        this.message = (message != null) ? message : this.message;
        this.address = (address != null) ? address : this.address;
        this.nickName = (nickName != null) ? nickName : this.nickName;
        this.role = Role.USER;
    }

    public void updateUserInfo(String address, String nickName, String message, String imageUrl) {
        this.message = (!message.isBlank()) ? message : this.message;
        this.address = (!address.isBlank()) ? address : this.address;
        this.nickName = (!nickName.isBlank()) ? nickName : this.nickName;
        this.imageURL = (imageUrl != null) ? imageUrl : this.imageURL;
    }
}