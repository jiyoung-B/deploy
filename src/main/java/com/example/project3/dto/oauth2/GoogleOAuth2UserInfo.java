package com.example.project3.dto.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 구글 Response JSON 예시
*{
 *    "sub": "식별값",
 *    "name": "name",
 *    "given_name": "given_name",
 *    "picture": "https//lh3.googleusercontent.com/~~",
 *    "email": "email",
 *    "email_verified": true,
 *    "locale": "ko"
 * }
*/
@Slf4j
public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {

        log.info("Google getId : {}", attributes.get("sub"));
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {

        log.info("Google getNickName : {}", attributes.get("name"));
        return (String) attributes.get("name");
    }



    @Override
    public String getImageUrl() {

        log.info("Google getImageUrl : {}", attributes.get("picture"));
        return (String) attributes.get("picture");
    }

    @Override
    public String getEmail() {

        log.info("Google getEmail : {}", attributes.get("email"));
        return (String) attributes.get("email");
    }
}