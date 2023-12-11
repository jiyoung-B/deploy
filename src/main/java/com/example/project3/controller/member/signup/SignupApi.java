package com.example.project3.controller.member.signup;

import com.example.project3.dto.request.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "회원가입")
public interface SignupApi {
    @Operation(summary = "회원가입", description = """
            multipart/form-data로 프로필 이미지로 사용할 파일과 회원가입 JSON데이터를 요청한다.
            이미지파일은 요청 안 하면 서버에서 기본 이미지로 저장한다.<br>
            회원가입 요청 데이터는 content-type을 "applcation/json"으로 명시해주어야 한다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signup Successful"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "401", description = "무효한 토큰으로 인증 불가능"),
            @ApiResponse(responseCode = "404", description = "유효한 토큰이나 토큰 정보로 유저 조회 불가능") })
    @Parameters({
            @Parameter(name = "request", description = "회원가입 요청(application/json)",
                    example = """
                                { userName : "" ,
                                  email : "",
                                  password : "",
                                  address : "",
                                  nickName : "",
                                  message : "" }
                              """),
            @Parameter(name = "file", description = "이미지파일") })
    ResponseEntity<?> signup(@Valid @RequestPart("request") SignupRequest request,
                             @RequestPart(value = "file", required = false) MultipartFile file);
}