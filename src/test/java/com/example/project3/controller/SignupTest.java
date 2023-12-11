package com.example.project3.controller;

import com.example.project3.entity.member.Member;
import com.example.project3.dto.request.SignupRequest;
import com.example.project3.repository.MemberRepository;
import com.example.project3.service.MemberService;
import com.example.project3.service.S3Uploader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SignupTest {

    private final static Faker faker = new Faker(new Locale("ko"));

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private S3Uploader s3Uploader;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        memberRepository.deleteAll();
        s3Uploader.deleteFile("image.jpg");
    }

    @Test
    @DisplayName("회원 가입에 성공한다.")
    void successSignUp() throws Exception{
        // given
        // Mock data
        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().emailAddress();
        String address = faker.address().fullAddress();
        String imageURL = faker.internet().avatar();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();

        SignupRequest signupRequest = new SignupRequest(username, email, "password12@",
                address, nickName, message);

        final String requestBody = objectMapper.writeValueAsString(signupRequest);

        // when

        ResultActions result = getSignupResult(signupRequest);
        Member member = memberRepository.findByEmail(signupRequest.getEmail()).get();

        // then
        // 비밀번호 암호화 테스트
        assertThat(member.getPassword()).isNotEqualTo(signupRequest.getPassword());

        result.andExpect(status().isOk())
                .andExpect(content().string("Signup Successful"));
    }

    @Test
    @DisplayName("회원 가입에 실패한다.(유효하지 않은 이메일과 비밀번호, 전화번호)")
    void failSignup() throws Exception{
        // given
        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().password();
        String password = faker.internet().password(8, 20);
        System.out.println("password = " + password);
        String address = faker.address().fullAddress();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();

        SignupRequest signupRequest = new SignupRequest(username, email, password,
                address, nickName, message);

        // when
        ResultActions result = getSignupResult(signupRequest);

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일 형식을 맞춰주세요."))
                .andExpect(jsonPath("$.password").value("8 ~ 20자, 최소 한개의 특수문자와 숫자, 영문 알파벳을 포함해야 함."));
    }



    @Test
    @DisplayName("회원 가입에 실패한다.(중복 회원)")
    void duplicateSignup() throws Exception{
        // given
        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8,15) + "12@";
        System.out.println("password = " + password);
        String address = faker.address().fullAddress();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();

        SignupRequest request = new SignupRequest(username, email, password,
                address, nickName, message);

        MockMultipartFile file = null;
        // when
        memberService.signup(request, file);

        Member member = memberRepository.findByEmail(request.getEmail()).get();

        // then
        // imageURL에 null값을 받았을 때, default로 설정한 URL이 들어갔는지 확인
        assertThat(member.getImageURL()).isEqualTo(MemberService.DEFAULT_IMAGE_URL);

        // when
        ResultActions result = getSignupResult(request);

        // then
        result.andExpect(status().isConflict())
                .andExpect(content().string("Email already exists"));
    }



    @DisplayName("로그인 성공, AccessToken과 RefreshToken 응답 완료")
    @Test
    void login() throws Exception {
        // given
        final String url = "/login";

        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().emailAddress();
        String address = faker.address().fullAddress();
        String imageURL = faker.internet().avatar();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();
        String password = "testPassword13@";

        SignupRequest request = new SignupRequest(username, email, password,
                address, nickName, message);

        // when
        getSignupResult(request); // 먼저 "/api/signup"으로 회원가입 신청

        LoginRequest loginRequest = new LoginRequest(email, password);

        final String requestBody = objectMapper.writeValueAsString(loginRequest);

        // then
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization_Access_Token", is(not(""))))
                .andExpect(header().string("Authorization_Refresh_Token", is(not(""))));
    }


    private ResultActions getSignupResult(SignupRequest signupRequest) throws Exception {

        String requestBody = objectMapper.writeValueAsString(signupRequest);

        MockMultipartFile request = new MockMultipartFile("request", "","application/json", requestBody.getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg","content".getBytes());


        ResultActions result = mockMvc.perform(multipart("/api/signup")
                        .file(file)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                        .andDo(print());
        return result;
    }

    @AllArgsConstructor
    @Getter
    public static class LoginRequest {
        private String email;
        private String password;
    }
}

