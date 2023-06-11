package com.jscode.demoApp.controller;

import com.jscode.demoApp.config.SecurityConfig;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.service.MemberService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import({SecurityConfig.class})
public class TestSecurityConfig {

    @MockBean
    JwtTokenProvider jwtTokenProvider;
    @MockBean
    MemberService memberService;

    //Import한 SecurityConfig.class의 UserDetailService를 이용한 @UserDetails는 안 되는 듯 하다.

    @BeforeTestMethod
    public void init(){
        given(memberService.findMemberByEmail(anyString())).willReturn(MemberDto.of(1L, "sss@naver.com", "123123", LocalDateTime.now()));
    }

    @Primary
    @Bean
    public UserDetailsService testDetailService(){
        return email -> UserPrincipal.of(1L, email, "123455678");
    }


}
