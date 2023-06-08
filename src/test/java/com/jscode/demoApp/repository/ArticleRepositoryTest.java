package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.request.PageRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Import({TestJpaConfig.class, ArticleRepositoryWithVanillaJpa.class})
@ActiveProfiles("test")
@DataJpaTest
public class ArticleRepositoryTest {

    @Autowired private ArticleRepository articleRepository;

    private final Member defaultMember;
    private final PageRequest defaultPageRequest;

    {
        defaultMember = Member.builder()
                .id(1L)
                .email("sls@naver.com")
                .password("123456789")
                .build();

        defaultPageRequest = new PageRequest(0, 100);
    }

    public void saveArticles(){
        for(int i = 0; i < 10; i++){
            String title = "제목 " + i;
            String content = "내용 " + i;

            Article article = Article.builder()
                                    .title(title)
                                    .content(content)
                                    .member(defaultMember)
                                    .build();
            articleRepository.save(article);
        }
    }
    @DisplayName("[R]게시물 Id로 조회 성공 테스트")
    @Test
    @Transactional
    public void articleFindFailTest(){
        //given

        //when
        Optional<Article> article = articleRepository.findById(2L);


        //then
        Assertions.assertThat(article.isEmpty()).isTrue();
    }

    @DisplayName("[R]게시물 Id로 조회 실패 테스트")
    @Test
    @Transactional
    public void articleFindTest(){
        //given
        Article newArticle = Article.builder()
                .title("제목 100")
                .content("내용")
                .member(defaultMember)
                .build();
        articleRepository.save(newArticle);

        //when
        Article article = articleRepository.findById(newArticle.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        Assertions.assertThat(article).isNotNull();
    }

    @DisplayName("[R]전체 게시물 조회 테스트")
    @Test
    @Transactional
    public void articleFindAllTest(){
        //given
        Long totalCount = articleRepository.getCount();
        saveArticles();


                //when
        List<Article> articles = articleRepository.findAll(defaultPageRequest);

        //then
        articles.stream().forEach(System.out::println);
        Assertions.assertThat((long) articles.size()).isEqualTo(totalCount + 10);
    }

    @DisplayName("[D]게시물 삭제 테스트")
    @Test
    @Transactional
    public void deleteArticleTest(){
        //given
        Article newArticle = Article.builder()
                .title("제목 100")
                .content("내용")
                .member(defaultMember)
                .build();
        articleRepository.save(newArticle);
        Long deletedId = newArticle.getId();

        //when
        articleRepository.delete(newArticle);

        //then
        Optional<Article> article = articleRepository.findById(deletedId);
        Assertions.assertThat(article.isPresent()).isFalse();
    }

    @DisplayName("[R]제목 검색 테스트")
    @Test
    @Transactional
    public void findByTitleTest(){
        //given
        Article article1 = Article.builder().title("검색해줘").content("검색 내용").member(defaultMember).build();
        Article article2 = Article.builder().title("검색해줘").content("검색 내용").member(defaultMember).build();
        articleRepository.save(article1);
        articleRepository.save(article2);

        //when
        List<Article> searchedArticles = articleRepository.findByTitle("검색해줘", defaultPageRequest);

        //then
        Assertions.assertThat(searchedArticles.size()).isEqualTo(2);
    }

}
