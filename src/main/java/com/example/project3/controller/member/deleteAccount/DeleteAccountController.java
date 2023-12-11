package com.example.project3.controller.member.deleteAccount;

import com.example.project3.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeleteAccountController implements DeleteAccountApi{

    private final MemberService memberService;
    @Override
    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteAccount(UserDetails userDetails, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");

        memberService.deleteAccount(userDetails.getUsername(), accessToken);
        return ResponseEntity.ok().build();
    }
}