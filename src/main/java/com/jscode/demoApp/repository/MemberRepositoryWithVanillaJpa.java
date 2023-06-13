package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class MemberRepositoryWithVanillaJpa implements MemberRepository{

    @PersistenceContext
    EntityManager em;

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public void delete(Member member) {
        em.remove(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        Member member = em.createQuery("SELECT m " +
                "FROM Member m " +
                "WHERE m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByEmailAndPassword(String email, String password) {
        Member member = em.createQuery("SELECT m " +
                "FROM Member m " +
                "WHERE m.email = :email AND m.password = :password", Member.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> getReferenceById(Long memberId) {
        return Optional.ofNullable(em.getReference(Member.class, memberId));
    }
}
