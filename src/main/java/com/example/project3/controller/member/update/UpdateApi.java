package com.example.project3.controller.member.update;

import com.example.project3.dto.request.UpdateUserInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "회원정보 수정")
public interface UpdateApi {
    @Operation(summary = "회원정보 수정(토큰 필요)", description = """
                        multipart/form-data로 이미지 파일과 회원정보 수정 JSON데이터를 요청한다.
                        회원정보 수정 요청 데이터는 content-type을 "applcation/json"으로 명시해주어야 한다.
                        """ )
    @Parameters({
            @Parameter(name = "request",description = "회원정보 수정 요청(application/json)",
                    schema = @Schema(type = "UpdateUserInfoRequest"), example = """
                    {
                        address : "", nickName : "", message : ""
                    }
                    """ ) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "무효한 토큰으로 인증 불가능"),
            @ApiResponse(responseCode = "404", description = "유효한 토큰이나 토큰 정보로 유저 조회 불가능"),
    })
    @PatchMapping("/user")
    ResponseEntity<Void> updateUserInfo(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
                                        @RequestPart(value = "request",required = false) UpdateUserInfoRequest request,
                                        @RequestPart(value = "file",required = false) MultipartFile file);
}