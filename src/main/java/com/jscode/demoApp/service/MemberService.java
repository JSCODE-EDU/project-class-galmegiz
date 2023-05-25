package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.MemberRegisterRequest;
import com.jscode.demoApp.error.exception.LoginFailException;
import com.jscode.demoApp.error.exception.MemberDuplicateException;
import com.jscode.demoApp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    public MemberDto register(MemberDto memberDto) {
        memberRepository.findByEmail(memberDto.getEmail())
                .ifPresent(member  -> {throw new MemberDuplicateException(member.getEmail());});

        Member member = Member.builder()
                                .email(memberDto.getEmail())
                                .password(memberDto.getPassword())
                                .build();
        return MemberDto.fromEntity(memberRepository.save(member));
    }

    public MemberDto login(MemberDto memberDto){
        //다 exception처리를 하는 게 맞을까? false return해서 사용자 응답만 보내주게 하는 방법은? 익셉션이 너무 늘어나고 있다.
        return memberRepository.findByEmailAndPassword(memberDto.getEmail(), memberDto.getPassword())
                .map(MemberDto::fromEntity)
                .orElseThrow(LoginFailException::new);
    }
}
