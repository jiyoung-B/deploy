package com.example.project3.controller;

import com.example.project3.entity.member.Member;
import com.example.project3.controller.SignupTest.LoginRequest;
import com.example.project3.dto.request.SignupRequest;
import com.example.project3.dto.request.UpdateUserInfoRequest;
import com.example.project3.dto.response.MemberInfoResponse;
import com.example.project3.repository.MemberRepository;
import com.example.project3.service.MemberService;
import com.example.project3.service.S3Uploader;
import com.example.project3.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberTest {

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

    @Autowired
    private RedisUtil redisUtil;

    private SignupRequest signupRequest;

    // TODO : 파일 저장하는 법
    @BeforeEach
    void set() throws Exception {
         signupRequest = SignupRequest.builder()
                                       .email("test@test.com")
                                       .message("한줄메시지")
                                       .password("password12@")
                                       .nickName("별명1")
                                       .username("사용자1")
                                       .address("서울특별시")
                                       .build();
        memberService.signup(signupRequest, null);
    }

    @AfterEach
    void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                        .addFilter(new CharacterEncodingFilter("UTF-8", true))
                                        .alwaysDo(print()).build();
        memberRepository.deleteAll();
        s3Uploader.deleteFile("image.jpg");
    }

    @Test
    void 로그아웃() throws Exception {
        // given
        String accessToken = getAccessToken();

        // then
        mockMvc.perform(post("/api/logout")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        // 검증: 로그아웃 이후의 동작 확인
        // 1. RefreshToken 비워지는지 확인
        Member member = memberRepository.findByEmail(signupRequest.getEmail()).orElse(null);

        assertThat(member).isNotNull();
        assertThat(member.getRefreshToken()).isNull();

        // 2. SecurityContextHolder의 Authentication 객체가 사라지는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // 3. Redis에 액세스 토큰이 블랙리스트로 등록되는지 확인
        assertThat(accessToken).isNotNull();
        assertThat(redisUtil.hasKeyBlackList(accessToken.substring(7))).isTrue();
    }


    @Test
    void 회원정보_조회() throws Exception {
        // given
        String accessToken = getAccessToken();

        // when
        MvcResult memberInfoResult = mockMvc.perform(get("/api/user")
                                            .header("Authorization", accessToken)
                                            .contentType(MediaType.APPLICATION_JSON))
                                            .andExpect(status().isOk())
                                            .andReturn();

        String contentAsString = memberInfoResult.getResponse().getContentAsString();
        // then
        MemberInfoResponse memberInfoResponse = objectMapper.readValue(contentAsString, MemberInfoResponse.class);

        assertThat(memberInfoResponse.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(memberInfoResponse.getMessage()).isEqualTo(signupRequest.getMessage());
        assertThat(memberInfoResponse.getNickName()).isEqualTo(signupRequest.getNickName());
        assertThat(memberInfoResponse.getName()).isEqualTo(signupRequest.getUserName());
        assertThat(memberInfoResponse.getAddress()).isEqualTo(signupRequest.getAddress());
        assertThat(memberInfoResponse.getImageUrl()).isNotNull();
        assertThat(memberInfoResponse.getSimplifiedPostResponseList()).isEmpty();
        assertThat(memberInfoResponse.getSocialId()).isNull();
        assertThat(memberInfoResponse.getSocialType()).isNull();
    }


    @Test
    void 회원탈퇴() throws Exception {
        // given
        String accessToken = getAccessToken();

        // when
         mockMvc.perform(delete("/api/user")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // then

        Member member = memberRepository.findByEmail(signupRequest.getEmail()).orElse(null);
        assertThat(member).isNull();

        assertThat(accessToken).isNotNull();
        assertThat(redisUtil.hasKeyBlackList(accessToken.substring(7))).isTrue();
    }

    @Test
    void 회원정보_수정() throws Exception {
        // given
        String accessToken = getAccessToken();

        UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest("인천광역시", "별명3", "좋아요");
        final String requestBody = objectMapper.writeValueAsString(updateUserInfoRequest);

        MockMultipartFile request = new MockMultipartFile("request", "","application/json", requestBody.getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg","content".getBytes());



        // when
       mockMvc.perform(multipart(HttpMethod.PATCH, "/api/user")
              .file(file)
              .file(request)
              .header("Authorization", accessToken)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

        // then
        memberRepository.findByEmail(signupRequest.getEmail())
                .ifPresent(member -> {
                    assertThat(member.getAddress()).isEqualTo(updateUserInfoRequest.getAddress());
                    assertThat(member.getNickName()).isEqualTo(updateUserInfoRequest.getNickName());
                    assertThat(member.getMessage()).isEqualTo(updateUserInfoRequest.getMessage());
                });
    }

    @Test
    void 닉네임_중복_확인() throws Exception {
        // given
        String checkNickname = "별명1";
        // when
        // 닉네임 중복인 경우
        mockMvc.perform(post("/api/user/{nickName}",checkNickname))
                .andExpect(status().isConflict());
        // then
        // 중복되지 않은 경우
        mockMvc.perform(post("/api/user/{nickName}","별명3"))
                .andExpect(status().isOk());
    }



    @Nullable
    private String getAccessToken() throws Exception {
        MvcResult result = getResult();

        return result.getResponse().getHeader("Authorization_Access_Token");
    }


    @NotNull
    private MvcResult getResult() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@test.com", "password12@");
        final String requestBody = objectMapper.writeValueAsString(loginRequest);
        // when
        return mockMvc.perform(post("/login")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .content(requestBody))
                                  .andExpect(status().isCreated())
                                  .andReturn();
    }
}
