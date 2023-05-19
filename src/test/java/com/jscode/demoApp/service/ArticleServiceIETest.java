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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
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
            articleService.createArticle(ArticleDto.fromEntity(article));
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
        ArticleDto newArticle = articleService.createArticle(ArticleDto.fromEntity(article));
        System.out.println("newArticle = " + newArticle.getId());
        ArticleDto findArticle = articleService.getArticle(newArticle.getId());
        Assertions.assertThat(findArticle.getTitle()).isEqualTo(title);
    }

    @DisplayName("[조회] 게시글 찾기 실패 테스트")
    @Test
    public void getArticleFailTest(){
        Assertions.assertThatThrownBy(() -> {
            articleService.getArticle(100L);})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("[조회] 전체 게시글 조회 테스트")
    @Test
    public void getAllArticleTest(){
        saveArticles();
        List<ArticleDto> articles = articleService.getAllArticles();
        articles.stream().forEach(System.out::println);
        Assertions.assertThat(articles.size()).isEqualTo(10);
    }

    @DisplayName("[삭제] 게시글 삭제 테스트")
    @Test
    public void deleteArticleTest(){
        saveArticles();
        String title = "제목 수정 전";
        String content = "내용 수정 전";
        ArticleDto articleDto = new ArticleDto(null, title, content);
        ArticleDto newArticle = articleService.createArticle(articleDto);
        Long id = newArticle.getId();


        articleService.deleteArticle(id);
        List<ArticleDto> articles = articleService.getAllArticles();
        Assertions.assertThat(articles.size()).isEqualTo(10);
    }


    @DisplayName("[수정] 게시글 수정 테스트")
    @Test
    public void updateArticleTest(){
        String title = "제목 수정 전";
        String content = "내용 수정 전";
        ArticleDto articleDto = new ArticleDto(null, title, content);
        ArticleDto newArticle = articleService.createArticle(articleDto);
        Long id = newArticle.getId();


        ArticleDto updatedArticleDto = new ArticleDto(id, "제목 수정 후", "내용 수정 후");
        ArticleDto result = articleService.updateArticle(updatedArticleDto);
        System.out.println("result.getTitle() = " + result.getTitle());
        Assertions.assertThat(articleService.getArticle(id).getTitle()).isEqualTo(result.getTitle());

    }

    @DisplayName("[검색] 게시물 검색 테스트")
    @Test
    public void articleSearchTest(){
        String title = "찾을 제목";
        String content = "내용 무";

        IntStream.range(0, 5).forEach(i -> articleService.createArticle(new ArticleDto(null, title, content)));


        List<ArticleDto> articles = articleService.searchArticle(new SearchRequestDto(SearchType.TITLE, title));
        Assertions.assertThat(articles.size()).isEqualTo(5);

    }


}
