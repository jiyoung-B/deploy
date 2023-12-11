package com.example.project3.jwt;

import com.example.project3.entity.member.Member;
import com.example.project3.entity.member.Role;
import com.example.project3.config.jwt.JwtProperties;
import com.example.project3.config.jwt.TokenProvider;
import com.example.project3.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
 class TokenProviderTest {

    private static final Faker faker = new Faker(new Locale("ko"));

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @AfterEach
    void init() {
        memberRepository.deleteAll();
    }

    @DisplayName("토큰 생성")
    @Test
    void generateToken() {
        // given
        // Mock data
        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().emailAddress();
        String address = faker.address().fullAddress();
        String imageURL = faker.internet().avatar();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();

       Member testMember = Member.builder()
               .name(username)
               .email(email)
               .address(address)
               .imageURL(imageURL)
               .nickName(nickName)
               .message(message)
               .password("testPassword13@")
               .role(Role.USER)
               .build();

       memberRepository.save(testMember);

       Member member = memberRepository.findByEmail(email).orElseThrow(
                ()->new IllegalArgumentException("Unexpected"));

        // when
        String token = tokenProvider.createAccessToken(testMember);

        // then

        Long userId = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes()))
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(member.getId());
    }

    @DisplayName("만료된 토큰 검증")
    @Test
    void invalidToken() {
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        // when
        boolean result = tokenProvider.validToken(token, request);
        // then
        assertThat(result).isFalse();
    }
    @DisplayName("유효한 토큰 검증")
    @Test
    void validToken() {
        // given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        // when
        boolean result = tokenProvider.validToken(token, request);

        // then
        assertThat(result).isTrue();
    }
    @DisplayName("토큰으로 인증 정보 가져오기")
    @Test
    void getAuthentication() {
        // given
        String username = faker.name().lastName() + faker.name().firstName();
        String email = faker.internet().emailAddress();
        String address = faker.address().fullAddress();
        String imageURL = faker.internet().avatar();
        String nickName = faker.name().prefix() + faker.name().firstName();
        String message = faker.lorem().sentence();

        Member testMember = Member.builder()
                .name(username)
                .email(email)
                .address(address)
                .imageURL(imageURL)
                .nickName(nickName)
                .message(message)
                .password("testPassword13@")
                .role(Role.USER)
                .build();

        memberRepository.save(testMember);
        String token = JwtFactory.builder()
                .subject(email)
                .build()
                .createToken(jwtProperties);
        System.out.println("token = " + token);
        // when
        Authentication authentication = tokenProvider.getAuthentication(token);
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);
        // then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(email);
    }
    @DisplayName("토큰으로 Id 가져오기")
    @Test
    void getMemberId() {
        // given
        Long memberId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", memberId))
                .build()
                .createToken(jwtProperties);
        // when
        Long memberIdByToken = tokenProvider.getMemberId(token);

        // then
        assertThat(memberIdByToken).isEqualTo(memberId);
    }
}
