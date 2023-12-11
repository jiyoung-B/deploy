package com.example.project3.config.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("소셜 로그인에 실패했습니다, OAuth2LoginFailureHandler 실행");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            OAuth2Error oauth2Error = oauth2Exception.getError();

            if (oauth2Error != null && oauth2Error.getErrorCode().equals("DuplicateEmail")) {
                log.info(oauth2Error.toString());
                String errorMessage = "중복된 이메일입니다: " + oauth2Error.getDescription();
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("소셜 로그인 실패! " + oauth2Error.getDescription() + "으로 가입된 정보가 있습니다.");
                log.info("소셜 로그인에 실패했습니다. 에러 메시지: {}", errorMessage);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("소셜 로그인 실패! 서버 로그를 확인해주세요.");
                log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());
            }
        }
    }
}