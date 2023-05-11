package com.jscode.demoApp.service;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceUnitTest {
    @InjectMocks private ArticleService articleService;
    @Mock private ArticleRepository articleRepository;

    @Test
    @DisplayName("id로 게시글 찾기 테스트")
    public void findByIdFailTest(){
        Long id = 1L;
        given(articleRepository.findById(id)).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> articleService.getArticle(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("저장 테스트")
    public void saveTest(){
        Article article = Article.builder().title("제목").content("내용").build();
        given(articleRepository.save(any(Article.class))).willReturn(article);

        articleService.createArticle(ArticleDto.fromEntity(article));
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("검색 테스트")
    public void searchTest(){
        given(articleRepository.findByTitle(any(String.class))).willReturn(new ArrayList<Article>());
        articleService.searchArticle(SearchType.TITLE, "searchKeyworkd");
        articleService.searchArticle(null, "searchKeyworkd");

        then(articleRepository).should().findByTitle(any(String.class));
        then(articleRepository).should().findAll();
    }
}
