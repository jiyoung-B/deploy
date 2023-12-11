package com.example.project3.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.error("알 수 없는 에러");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        else{
            log.info("로그아웃 처리되었습니다.");
            // 기본 응답 값 : 200 OK
        }

    }
}
