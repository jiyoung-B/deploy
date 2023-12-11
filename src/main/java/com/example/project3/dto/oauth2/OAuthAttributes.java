package com.example.project3.dto.oauth2;

import com.example.project3.entity.member.Member;
import com.example.project3.entity.member.Role;
import com.example.project3.entity.member.SocialType;
import com.example.project3.util.PasswordUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
* 각 소셜에서 받아오는 데이터가 다르다.
 * 소셜별로 받는 데이터를 분기 처리하는 DTO
*/
@Getter
@AllArgsConstructor
@Builder
@Slf4j
public class OAuthAttributes {

    private String nameAttributeKey;
    private OAuth2UserInfo oAuth2UserInfo;


    /**
     * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * 파라미터 : userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값 / attributes : OAuth 서비스의 유저 정보들
     * 소셜별 of 메소드(ofGoogle, ofKaKao)들은 각각 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
     */
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        log.info("OAuthAttributes 진입");
        log.info("SocialType : {}", socialType);
        log.info("userNameAttributes : {}", userNameAttributeName);
        log.info("attributes : {}", attributes);

        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
            return ofGoogle(userNameAttributeName, attributes);

    }



    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        log.info("ofKakao 실행");

        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        log.info("ofGoogle 실행");

        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    /**
     * of메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태
     * OAuth2UserInfo에서 socialId(식별값), nickname, imageUrl, email을 가져와서 build
     * role은 GUEST로 설정
     */
    public Member toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo) {
        log.info("처음 등록하려는 소셜 유저입니다, 임의의 비밀번호를 생성하여 GUEST로 정보를 저장 합니다.");
        return Member.builder()
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .imageURL(oAuth2UserInfo.getImageUrl())
                .role(Role.GUEST)
                .password(PasswordUtil.generateRandomPassword())
                .build();
    }
}