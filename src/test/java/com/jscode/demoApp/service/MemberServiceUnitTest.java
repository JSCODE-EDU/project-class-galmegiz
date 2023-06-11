package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.error.exception.MemberDuplicateException;
import com.jscode.demoApp.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberServiceUnitTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("[회원가입] 멤버 신규 생성 테스트(성공)")
    @Test
    public void memberRegisterTest(){
        String email = "somels@naver.com";
        String password = "12345678";

        given(memberRepository.save(any(Member.class))).willReturn(Member.builder()
                                                                            .email(email)
                                                                            .password(password)
                                                                            .build());

        MemberDto memberDto = MemberDto.of(email, password);
        MemberDto result = memberService.register(memberDto);

        Assertions.assertThat(result.getEmail()).isEqualTo(email);
    }

    @DisplayName("[회원가입] 멤버 신규 생성 테스트(실패:중복 아이디)")
    @Test
    public void memberRegisterFailTest(){
        String email = "som@naver.com";
        String password = "123456789";
        MemberDto memberDto = MemberDto.of(email, password);

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(Member
                                                                            .builder()
                                                                            .email(email)
                                                                            .password(password)
                                                                            .build()));
        Assertions.assertThatThrownBy(() -> memberService.register(memberDto))
                .isInstanceOf(MemberDuplicateException.class);
    }
/*

    @DisplayName("[로그인] 로그인 실패 테스트")
    @Test
    public void loginFailTest(){
        String email = "som@naver.com";
        String password = "123456789";
        MemberDto memberDto = MemberDto.of(email, password);

        given(memberRepository.findByEmailAndPassword(email, password)).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> memberService.login(memberDto))
                .isInstanceOf(LoginFailException.class);
    }
/*    @DisplayName("[로그인] 로그인 성공 테스트")
    @Test
    public void loginTest(){
        String email = "som@naver.com";
        String password = "123456789";
        MemberDto memberDto = MemberDto.of(email, password);

        given(memberRepository.findByEmailAndPassword(email, password))
                .willReturn(Optional.of(Member.builder()
                                                .email(email)
                                                .password(password)
                                                .build()
                                        ));

        Assertions.assertThat(memberService.login(memberDto).getEmail()).isEqualTo(memberDto.getEmail());
    }*/






}
