package com.example.project3.service;

import com.example.project3.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUserName 실행");

       return memberRepository.findByEmail(email).map(
               member -> {
                   log.info("UserDetails를 전달합니다.");
        //      org.springframework.security.core.userdetails.User
                   return User.builder()
                           .username(member.getEmail())
                           .password(member.getPassword())
                           .roles(member.getRole().getValue())
                           .build();
               }).orElseThrow(()->{
                    log.error("{}이 DB에서 조회되지 않습니다.", email);
                    return new UsernameNotFoundException("Cannot Found Member");
               });
    }
}