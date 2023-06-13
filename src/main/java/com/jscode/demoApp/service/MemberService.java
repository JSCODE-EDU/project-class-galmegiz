package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.LoginFailException;
import com.jscode.demoApp.error.exception.MemberDuplicateException;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
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

    /* login은 Spring security에서 담당
    public String login(MemberDto memberDto){

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(memberDto.getEmail(), memberDto.getPassword());
        Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authRequest);
        return tokenProvider.createToken(authResult);
    }
*/
    public MemberDto findMemberByEmail(String email){
        return memberRepository.findByEmail(email).map(MemberDto::fromEntity)
                .orElseThrow(LoginFailException::new);
    }

    public MemberDto findMemberById(Long id){
        return memberRepository.findById(id).map(MemberDto::fromEntity)
                .orElseThrow(() -> {throw new ResourceNotFoundException(ErrorCode.MEMBER_NOT_FOUND);});
    }
}
