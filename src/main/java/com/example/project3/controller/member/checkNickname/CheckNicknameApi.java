package com.example.project3.controller.member.checkNickname;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Tag(name = "닉네임 중복 확인")
public interface CheckNicknameApi {

    @Operation(summary = "닉네임 중복 확인", description = "회원가입 시도 시 사용가능한 닉네임인지 중복확인")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "닉네임 중복"),
            @ApiResponse(responseCode = "200", description = "성공") })
    @Parameters({
            @Parameter(name = "nickName", description = "중복 확인할 닉네임", in = PATH, schema = @Schema(type = "string")) })
    ResponseEntity<Void> isDuplicatedNickname(@PathVariable(name = "nickName") String nickName);
}