package com.jscode.demoApp.service;

import com.jscode.demoApp.config.SecurityConfig;
import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.PageRequest;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.repository.ArticleRepository;
import com.jscode.demoApp.repository.TestJpaConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.IntStream;


@Disabled
@ActiveProfiles("test")
@Import({TestJpaConfig.class})
@EnableAutoConfiguration(exclude = SecurityConfig.class)
@SpringBootTest
public class ArticleServiceIETest {



    @Autowired
    private ArticleService articleService;
    @Autowired private ArticleRepository articleRepository;


    public void saveArticles(){
        for(int i = 0; i < 10; i++){
            String title = "제목 " + i;
            String content = "내용 " + i;
            Article article = Article.builder()
                    .title(title)
                    .content(content)
                    .build();
            articleService.createArticle(ArticleDto.of(null, "제목", "내용"), 1L);
        }
    }

    @DisplayName("[조회] id로 게시글 찾기 테스트")
    @Test
    public void getArticleTest(){
        System.out.println(articleRepository);
        String title = "제목 찾기";
        String content = "내용 찾기";
        Article article = Article.builder()
                .title(title)
                .content(content)
                .build();
        ArticleDto newArticle = articleService.createArticle(ArticleDto.fromEntity(article), 1L);
        System.out.println("newArticle = " + newArticle.getId());
        ArticleDto findArticle = articleService.getArticle(newArticle.getId());
        Assertions.assertThat(findArticle.getTitle()).isEqualTo(title);
    }

    @DisplayName("[조회] 게시글 찾기 실패 테스트")
    @Test
    public void getArticleFailTest(){
        Assertions.assertThatThrownBy(() -> {
            articleService.getArticle(100L);})
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("[조회] 전체 게시글 조회 테스트")
    @Test
    public void getAllArticleTest(){
        saveArticles();
        List<ArticleDto> articles = articleService.getAllArticles(new PageRequest(1, 100));
        articles.stream().forEach(System.out::println);
        Assertions.assertThat(articles.size()).isEqualTo(10);
    }

    @DisplayName("[삭제] 게시글 삭제 테스트")
    @Test
    public void deleteArticleTest(){
        saveArticles();
        String title = "제목 수정 전";
        String content = "내용 수정 전";
        ArticleDto articleDto = ArticleDto.of(null, title, content);
        ArticleDto newArticle = articleService.createArticle(articleDto, 1L);
        Long id = newArticle.getId();


        articleService.deleteArticle(id, 1L);
        List<ArticleDto> articles = articleService.getAllArticles(new PageRequest(1, 100));
        Assertions.assertThat(articles.size()).isEqualTo(10);
    }


    @DisplayName("[수정] 게시글 수정 테스트")
    @Test
    public void updateArticleTest(){
        String title = "제목 수정 전";
        String content = "내용 수정 전";
        ArticleDto articleDto = ArticleDto.of(null, title, content);
        ArticleDto newArticle = articleService.createArticle(articleDto, 1L);
        Long id = newArticle.getId();


        ArticleDto updatedArticleDto = ArticleDto.of(id, "제목 수정 후", "내용 수정 후");
        ArticleDto result = articleService.updateArticle(updatedArticleDto, 1L);
        System.out.println("result.getTitle() = " + result.getTitle());
        Assertions.assertThat(articleService.getArticle(id).getTitle()).isEqualTo(result.getTitle());

    }

    @DisplayName("[검색] 게시물 검색 테스트")
    @Test
    public void articleSearchTest(){
        String title = "찾을 제목";
        String content = "내용 무";

        IntStream.range(0, 5).forEach(i -> articleService.createArticle(ArticleDto.of(null, title, content), 1L));


        List<ArticleDto> articles = articleService.searchArticle(new SearchRequestDto(SearchType.TITLE, title), new PageRequest(1, 100));
        Assertions.assertThat(articles.size()).isEqualTo(5);

    }


}
