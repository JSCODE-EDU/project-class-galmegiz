package com.jscode.demoApp.repository;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.request.PageRequest;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    public Article save(Article article);
    public List<Article> findAll(PageRequest pageRequest);
    public Optional<Article> findById(Long id);
    public List<Article> findByTitle(String title, PageRequest pageRequest);
    public void delete(Article article);
    public Long getCount();

    public Optional<Article> getReferenceById(Long articleId);

    public List<Article> findBySearchType(SearchType searchtype, String SearchKeyword, PageRequest pageRequest);
}
