package com.example.project3.controller.member.getInfo;

import com.example.project3.dto.response.MemberInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "회원 정보 조회")
public interface GetInfoApi {

    @Operation(summary = "회원 정보 조회(토큰 필요)", description = "기본적인 회원 정보와 등록했던 글 응답\n" +
            "페이징 가능, default 페이징사이즈 : 10")
    @Parameters({
            @Parameter(name = "page", schema = @Schema(type = "integer"), in = QUERY,
                    description = "페이지 번호 (0부터 시작)"),
            @Parameter(name = "size", schema = @Schema(type = "integer"), in = QUERY,
                    description = "페이지 크기")
    }) @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "무효한 토큰으로 인증 불가능"),
            @ApiResponse(responseCode = "404", description = "유효한 토큰이나 토큰 정보로 유저 조회 불가능") })
    ResponseEntity<MemberInfoResponse> getMemberInfo(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
                                                     @Parameter(hidden = true) @PageableDefault Pageable pageable);
}