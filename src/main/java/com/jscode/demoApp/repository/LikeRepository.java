package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.ArticleMemberLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<ArticleMemberLike, Long> {
    Optional<ArticleMemberLike> findByArticle_IdAndMember_Id(Long articleId, Long memberId);
}
