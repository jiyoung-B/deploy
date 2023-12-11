package com.example.project3.controller.member.update;

import com.example.project3.dto.request.UpdateUserInfoRequest;
import com.example.project3.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UpdateController implements UpdateApi{

    private final MemberService memberService;
    @Override
    @PatchMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserInfo(UserDetails userDetails, UpdateUserInfoRequest request, MultipartFile file) {
        String email = userDetails.getUsername();
        memberService.updateUserInfo(email, request, file);

        return ResponseEntity.ok().build();
    }
}