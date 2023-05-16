package com.jscode.demoApp.service;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
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
            //아래 코드로도 예외처리 가능
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.", new EntityNotFoundException());
        });
        return ArticleDto.fromEntity(article);
    }

    public List<ArticleDto> searchArticle(SearchType searchType, String searchKeyword){
        List<Article> articles = new ArrayList<>();

        if(searchType == null || StringUtils.isEmptyOrWhitespace(searchKeyword)){
            articles = articleRepository.findAll();
        }else if(searchType == SearchType.TITLE){
            articles = articleRepository.findByTitle(searchKeyword);
        }else{ //ToDo : 향후 구현
            articles = articleRepository.findAll();
        }

        return articles.stream().map(ArticleDto::fromEntity).toList();
    }
    //getArticles를 빼는 게 나을까?
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
        //Todo : findById() 메소드 자체에서 값이 없을 경우 EntityNotFoundException을 throw하면 코드 중복을 줄일 수 있을 듯 하다.
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
