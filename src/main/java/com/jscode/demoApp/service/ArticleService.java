package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional //레포지토리에 태그다는 건 안 되는가?
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleDto getArticle(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
            throw new EntityNotFoundException("게시글이 존재하지 않습니다");
        });
        return ArticleDto.fromEntity(article);
    }

    public List<ArticleDto> getAllArticles(){
        List<Article> articles = articleRepository.findAll();
        return articles.stream().map(ArticleDto::fromEntity).toList();
    }

    public ArticleDto createArticle(ArticleDto articleDto){ //메소드명은 save로 통일하는 게 좋은가?
        Article newArticle = Article.builder()
                                    .title(articleDto.getTitle())
                                    .content(articleDto.getContent())
                                    .build();
        articleRepository.save(newArticle);
        return ArticleDto.fromEntity(newArticle);
    }

    public void deleteArticle(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("해당 게시글은 존재하지 않습니다");
                });
        articleRepository.delete(article);
    }

    public ArticleDto updateArticle(ArticleDto articleDto){
        Article article = articleRepository.findById(articleDto.getId())
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("해당 게시글은 존재하지 않습니다");
                });
        article.update(articleDto.getTitle(), articleDto.getContent());
        return ArticleDto.fromEntity(article);
    }


}
