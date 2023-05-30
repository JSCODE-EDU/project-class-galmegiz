package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepositoryWithSpring extends JpaRepository<Member, Long> {
}
