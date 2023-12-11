package com.example.project3.controller.member.deleteAccount;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "회원탈퇴")
public interface DeleteAccountApi {

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "무효한 토큰으로 인증 불가능"),
            @ApiResponse(responseCode = "404", description = "유효한 토큰이나 토큰 정보로 유저 조회 불가능"),
    })
    @Operation(summary = "회원탈퇴(토큰 필요)", description = "DB에서 회원 정보와 등록했던 글 영구적으로 삭제, 사용한 액세스 토큰은 다시는 사용 불가능")
    ResponseEntity<Void> deleteAccount(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
                                              HttpServletRequest request);
}