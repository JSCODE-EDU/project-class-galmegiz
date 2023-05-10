package com.jscode.demoApp.repository;

import com.jscode.demoApp.config.JpaConfig;
import com.jscode.demoApp.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Import({JpaConfig.class, ArticleRepositoryWithVanillaJpa.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ArticleRepositoryTest {

    @Autowired  private ArticleRepository articleRepository;
    @BeforeEach
    public void saveArticles(){
        for(int i = 0; i < 10; i++){
            String title = "제목 " + i;
            String content = "내용 " + i;
            Article article = Article.builder()
                                    .title(title)
                                    .content(content)
                                    .build();
            articleRepository.save(article);
        }
    }
    @DisplayName("게시물 조회 테스트")
    @Test
    @Transactional
    public void articleFindTest(){
        Article newArticle = Article.builder()
                .title("제목 100")
                .content("내용")
                .build();
        articleRepository.save(newArticle);
        Article article = articleRepository.findById(newArticle.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException();
        });
        System.out.println("article = " + article);
        Assertions.assertThat(article).isNotNull();
    }

    @DisplayName("전체 게시물 조회 테스트")
    @Test
    @Transactional
    public void articleFindAllTest(){
        List<Article> articles = articleRepository.findAll();
        System.out.println("========");
        articles.stream().forEach(System.out::println);
        Assertions.assertThat(articles.size()).isEqualTo(10);
    }

    @DisplayName("게시물 삭제 테스트")
    @Test
    @Transactional
    public void deleteArticleTest(){
        Article newArticle = Article.builder()
                                    .title("삭제 예정")
                                    .content("삭제 예정")
                                    .build();
        articleRepository.save(newArticle);
        Long deletedId = newArticle.getId();
        articleRepository.delete(newArticle);
        Optional<Article> article = articleRepository.findById(deletedId);
        Assertions.assertThat(article.isPresent()).isFalse();
    }
}
