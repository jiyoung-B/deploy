package com.example.project3.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "회원가입 요청 정보")
@ToString
public class SignupRequest extends UpdateUserInfoRequest{

    @Schema(description = "이름", example = "사용자1")
    @NotBlank(message = "Username is required") // "", " ", null 허용 안함.
    private String userName;

    @Schema(description = "이메일(유효성 검사 : 이메일 형식)",  example = "test@email.com")
    @NotBlank(message = "Email is required") // "", " ", null 허용 안함.
    @Email(message = "이메일 형식을 맞춰주세요.")
    private String email;

    @Schema(description = "비밀번호(유효성 검사 : 8 ~ 20자, 최소 한개의 특수문자와 숫자, 영문 알파벳을 포함)", example = "password12@")
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*?])(?=.*[a-zA-Z]).{8,20}$",
            message = "8 ~ 20자, 최소 한개의 특수문자와 숫자, 영문 알파벳을 포함해야 함.")
    private String password;

    @Builder
    // 테스트 코드용
    public SignupRequest(String username, String email, String password, String address, String nickName, String message) {
        super(address,nickName,message);
        this.password = password;
        this.userName = username;
        this.email = email;
    }
}