package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    public Article save(Article article);
    public List<Article> findAll();
    public Optional<Article> findById(Long id);
    public void delete(Article article);

}
