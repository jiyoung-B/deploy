package com.example.project3.config.login;

import com.example.project3.repository.MemberRepository;
import com.example.project3.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        log.info("로그인에 성공해 LoginSuccessHandler가 실행됩니다.");
        log.info("CustomJsonUsernamePasswordAuthenticationFilter에서 넘어온 인증 정보 : {}", authentication);

        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        String accessToken = tokenService.createAccessToken(email); // tokenService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = tokenService.createRefreshToken(); // tokenService의 createRefreshToken을 사용하여 RefreshToken 발급

        tokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

        // 멤버 조회 후 생성된 RefreshToken을 DB에 저장
        log.info("추출된 이메일 '{}' 로 DB에서 찾은 후, RefreshToken 저장", email);
        memberRepository.findByEmail(email)
                        .ifPresent(member -> {
                            member.updateRefreshToken(refreshToken);
                            memberRepository.save(member);
                        });

        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("RefreshToken이 DB에 저장되었습니다.");
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        log.info("인증 정보에서 추출된 userEmail : {}", userDetails.getUsername());
        log.info("인증 정보에서 추출된 authorities : {}",  userDetails.getAuthorities());

        return userDetails.getUsername();
    }
}
