package com.example.project3.config.login;

import com.example.project3.dto.oauth2.CustomOAuth2User;
import com.example.project3.entity.member.Role;
import com.example.project3.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");
        log.info("OAuth2LoginSuccessHandler가 실행됩니다");

        try{
            CustomOAuth2User oAuth2User =(CustomOAuth2User) authentication.getPrincipal();

            // Role이 GUEST일 경우 처음 요청한 회원이므로 추가정보를 위해 회원가입 페이지 리다이렉트
            if (oAuth2User.getRole() == Role.GUEST) {
                String accessToken = tokenService.createAccessToken(extractEmail(oAuth2User));

                tokenService.sendAccessAndRefreshToken(response, accessToken, null);

            } else loginSuccess(response,oAuth2User);
        }catch(Exception e){
            throw e;
        }



    }

    // TODO : 소셜 로그인 시에 무조건 토큰 생성 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) {
        String accessToken = tokenService.createAccessToken(extractEmail(oAuth2User));
        String refreshToken = tokenService.createRefreshToken();

        tokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        tokenService.updateRefreshToken(extractEmail(oAuth2User), refreshToken);
    }

    private String extractEmail(CustomOAuth2User oAuth2User) {
        log.info("oAuth2User.getAttributes : {}", oAuth2User.getAttributes());

        String email = null;

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Google 계정 정보에서 이메일 추출
        if (attributes.containsKey("email")) {
            email = (String) attributes.get("email");
        }

        // Kakao 계정 정보에서 이메일 추출
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            if (account.containsKey("email")) {
                email = (String) account.get("email");
            }
        }

        log.info("추출된 Email: {}", email);

        return email;
    }

}
