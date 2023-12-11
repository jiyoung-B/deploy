package com.example.project3.service;

import com.example.project3.dto.oauth2.CustomOAuth2User;
import com.example.project3.entity.member.Member;
import com.example.project3.entity.member.SocialType;
import com.example.project3.dto.oauth2.OAuthAttributes;
import com.example.project3.exception.DuplicateEmailException;
import com.example.project3.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
        try {
            /**
             * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
             * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
             * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
             * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
             */
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            /**
             * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
             * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
             * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
             */
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            log.info("registrationId : {}", registrationId);

            SocialType socialType = getSocialType(registrationId);

            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();// OAuth2 로그인 시 키(PK)가 되는 값

            Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

            // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
            OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

            Member member = getMember(extractAttributes, socialType); // getMember() 메소드로 User 객체 생성 후 반환

            // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
            return new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(member.getRole().getValue())),
                    attributes,
                    extractAttributes.getNameAttributeKey(),
                    member.getRole());
        } catch (DuplicateEmailException e) {
            throw new OAuth2AuthenticationException(new OAuth2Error("DuplicateEmail", e.getMessage(), null),e);
        }

    }

    private SocialType getSocialType(String registrationId) {
        if (KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private Member getMember(OAuthAttributes attributes, SocialType socialType) {

        String email = attributes.getOAuth2UserInfo().getEmail();

        return memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOAuth2UserInfo().getId())
                .map(member -> {
                    log.info("findBySocialTypeAndSocialId로 조회된 회원: {}", member.getSocialId());
                    return member;
                })
                .orElseGet(() -> {
                    if (!memberRepository.existsByEmail(email)) {
                        log.info("이메일 중복 확인 완료. 새로운 회원을 생성합니다.");
                        return saveMember(attributes, socialType);
                    } else {
                        log.error("이미 가입된 이메일입니다.");
                        throw new DuplicateEmailException(email);
                    }
                });
    }

    private Member saveMember(OAuthAttributes attributes, SocialType socialType) {
        Member member = attributes.toEntity(socialType, attributes.getOAuth2UserInfo());
        log.info("saveMember() 실행, 저장된 Member SocialId : {}", member.getSocialId());

        return memberRepository.save(member);
    }
}