package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    public Member save(Member member);
    public void delete(Member member);
    public Optional<Member> findById(Long id);
    public Optional<Member> findByEmail(String email);

    public Optional<Member> findByEmailAndPassword(String email, String password);

    public Optional<Member> getReferenceById(Long memberId);
}
