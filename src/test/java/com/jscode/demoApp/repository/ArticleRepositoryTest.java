package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;

@SpringBootTest
public class ArticleRepositoryTest {

    @Autowired  private ArticleRepository articleRepository;

    @DisplayName("게시물 저장 테스트")
    @Test
    public void saveAndFindTest(){
        Article article = Article.builder().title("제목")
               .content("내용").build();
        articleRepository.save(article);
        System.out.println(article.toString());
        Article article1 = articleRepository.findById(1L).orElseThrow(() -> {
            throw new EntityNotFoundException();
        });
        System.out.println(article1);
    }
}
