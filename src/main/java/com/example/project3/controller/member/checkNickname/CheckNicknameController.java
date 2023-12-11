package com.example.project3.controller.member.checkNickname;

import com.example.project3.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CheckNicknameController implements CheckNicknameApi{

    private final MemberService memberService;
    @Override
    @PostMapping("/user/{nickName}")
    public ResponseEntity<Void> isDuplicatedNickname(String nickName) {

        if (memberService.checkDuplicateNickname(nickName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        else return ResponseEntity.ok().build();
    }
}