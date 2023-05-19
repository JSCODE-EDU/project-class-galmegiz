package com.jscode.demoApp.service;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceUnitTest {
    @InjectMocks private ArticleService articleService;
    @Mock private ArticleRepository articleRepository;

    @Test
    @DisplayName("[검색]id로 게시글 찾기 테스트(실패)")
    public void findByIdFailTest(){
        Long id = 1L;
        given(articleRepository.findById(id)).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> articleService.getArticle(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("[저장]저장 테스트")
    public void saveTest(){
        Article article = Article.builder().title("제목").content("내용").build();
        given(articleRepository.save(any(Article.class))).willReturn(article);

        articleService.createArticle(ArticleDto.fromEntity(article));
        then(articleRepository).should().save(any(Article.class));
    }

    @ParameterizedTest(name = "[{index}] message : {0}({1})")
    @MethodSource("searchByTitleTest")
    @DisplayName("[검색]검색 테스트")
    public void searchByTest(String message, SearchType searchType, String searchKeyword){
        SearchRequestDto param = new SearchRequestDto(searchType, searchKeyword);

        if(searchType == null){
            given(articleRepository.findAll()).willReturn(new ArrayList<Article>());

            articleService.searchArticle(param);

            then(articleRepository).should().findAll();
        } else if (searchType == SearchType.TITLE) {
            given(articleRepository.findByTitle(anyString())).willReturn(new ArrayList<Article>());

            articleService.searchArticle(param);

            then(articleRepository).should().findByTitle(any(String.class));
        }

    }

    static Stream<Arguments> searchByTitleTest(){
        return Stream.of(
                arguments("Valid SearchType", SearchType.TITLE, "title1"),
                arguments("Short SearchKeyword", SearchType.TITLE, ""),
                arguments("No SearchType", null, null),
                arguments("No SearchType and Use keyword", null, "keyword")
        );
    }
}
