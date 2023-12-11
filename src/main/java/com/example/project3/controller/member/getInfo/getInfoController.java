package com.example.project3.controller.member.getInfo;

import com.example.project3.dto.response.MemberInfoResponse;
import com.example.project3.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class getInfoController implements GetInfoApi {

    private final MemberService memberService;
    @Override
    @GetMapping("/user")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(UserDetails userDetails, Pageable pageable) {
        MemberInfoResponse userInfo = memberService.getMemberInfo(userDetails.getUsername(), pageable);
        return ResponseEntity.ok().body(userInfo);
    }
}