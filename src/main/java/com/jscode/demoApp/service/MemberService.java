package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.LoginFailException;
import com.jscode.demoApp.error.exception.MemberDuplicateException;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    public MemberDto register(MemberDto memberDto) {
        memberRepository.findByEmail(memberDto.getEmail())
                .ifPresent(member  -> {throw new MemberDuplicateException(member.getEmail());});

        Member member = Member.builder()
                                .email(memberDto.getEmail())
                                .password(passwordEncoder.encode(memberDto.getPassword()))
                                .build();
        return MemberDto.fromEntity(memberRepository.save(member));
    }

    public String login(MemberDto memberDto){
        //다 exception처리를 하는 게 맞을까? false return해서 사용자 응답만 보내주게 하는 방법은? 익셉션이 너무 늘어나고 있다.
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(memberDto.getEmail(), memberDto.getPassword());
        Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authRequest);
        return tokenProvider.createToken(authResult);
    }

    public MemberDto findMemberByEmail(String email){
        return memberRepository.findByEmail(email).map(MemberDto::fromEntity)
                .orElseThrow(LoginFailException::new);
    }

    public MemberDto findMemberById(Long id){
        return memberRepository.findById(id).map(MemberDto::fromEntity)
                .orElseThrow(() -> {throw new ResourceNotFoundException(ErrorCode.MEMBER_NOT_FOUND);});
    }
}
