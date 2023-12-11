package com.example.project3.controller.member.logout;

import com.example.project3.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LogoutController implements LogoutApi{

    private final MemberService memberService;
    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(UserDetails userDetails, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");

        memberService.logout(userDetails, accessToken);
        return ResponseEntity.ok().build();
    }
}