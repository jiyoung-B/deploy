package com.example.project3.controller;

import com.example.project3.entity.member.Member;
import com.example.project3.entity.member.Role;
import com.example.project3.config.jwt.JwtProperties;
import com.example.project3.repository.MemberRepository;
import com.example.project3.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {

    private final static Faker faker = new Faker(new Locale("ko"));

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void setMockMvc() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        memberRepository.deleteAll();
    }

    @DisplayName("새로운 액세스 토큰 발급")
    @Test
    void createNewAccessToken() throws Exception {
        // given
        final String url = "/token";

        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().emailAddress();
        String address = faker.address().fullAddress();
        String imageURL = faker.internet().avatar();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();

        memberRepository.save(Member.builder()
                .name(username)
                .email(email)
                .address(address)
                .imageURL(imageURL)
                .nickName(nickName)
                .message(message)
                .password("testPassword13@")
                .role(Role.USER)
                .build());

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Unexpected"));

        String refreshToken = tokenService.createRefreshToken();

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);


        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .header("Authorization_Refresh_Token", "Bearer " + refreshToken))
                .andDo(print());  // refreshToken을 헤더에 추가


        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Authorization_Access_Token", is(not(""))))
                .andExpect(header().string("Authorization_Refresh_Token", is(not(""))));

    }
}

