package com.example.project3.config.jwt;

import com.example.project3.entity.member.Member;
import com.example.project3.exception.BlacklistedException;
import com.example.project3.exception.MissingTokenException;
import com.example.project3.service.MemberDetailService;
import com.example.project3.util.RedisUtil;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final MemberDetailService memberDetailService;
    private final RedisUtil redisUtil;
    private String secretKey;

    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);


    @PostConstruct
    protected void init() {
        log.info("TokenProvider init() 메소드 시작, secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes());
        log.info("TokenProvider secretKey 초기화 완료");
    }

    public String createAccessToken(Member member) {
       Date now = new Date();

        return Jwts.builder()
               .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
               .setIssuedAt(now)
               .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_DURATION.toMillis()))
               .setSubject(member.getEmail())
               .claim("id",member.getId())
               .signWith(SignatureAlgorithm.HS256, secretKey)
               .compact();
    }

    public String createRefreshToken() {
       Date now = new Date();

       return Jwts.builder()
               .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
               .setIssuedAt(now)
               .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_DURATION.toMillis()))
               .signWith(SignatureAlgorithm.HS256, secretKey)
               .compact();
    }

    public boolean validToken(String token, HttpServletRequest request) {
        try{
            if (redisUtil.hasKeyBlackList(token)) {
                log.error("블랙리스트 토큰");
                request.setAttribute("exception", new BlacklistedException("블랙리스트에 등록된 토큰입니다."));
                return false;
            }
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException e) {
            request.setAttribute("exception", e);
            log.error("토큰이 만료되었습니다. 에러 메시지: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", e);
            log.error("올바르지 않은 형식의 토큰입니다. 에러 메시지: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            request.setAttribute("exception", e);
            log.error("토큰 서명이 유효하지 않습니다. 에러 메시지: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", new MissingTokenException("서버에서 토큰이 추출되지 않습니다."));
            log.error("JWT 파싱 에러 : {}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        log.info("TokenProvider getAuthentication 실행");
        Claims claims = getClaims(token);
        String email = claims.getSubject();

        UserDetails userDetails = memberDetailService.loadUserByUsername(email);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public Long getMemberId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    public String getMemberEmail(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}