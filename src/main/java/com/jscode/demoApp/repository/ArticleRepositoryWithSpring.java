package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepositoryWithSpring extends JpaRepository<Article, Long> {
}
