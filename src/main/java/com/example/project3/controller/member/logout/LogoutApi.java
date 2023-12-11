package com.example.project3.controller.member.logout;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "로그아웃")
public interface LogoutApi {
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "무효한 토큰으로 인증 불가능"),
            @ApiResponse(responseCode = "404", description = "유효한 토큰이나 토큰 정보로 유저 조회 불가능"), })
    @Operation(summary = "로그아웃(토큰 필요)", description = "로그아웃 시도한 액세스 토큰은 다시는 사용 불가능")
    ResponseEntity<Void> logout(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails ,
                                       HttpServletRequest request);
}