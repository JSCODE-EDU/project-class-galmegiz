package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({CommentRepositoryWithVanillaJpa.class, TestJpaConfig.class})
@ActiveProfiles("test")
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired CommentRepository commentRepository;
    @PersistenceContext
    EntityManager em;


    public List<Comment> createComment(){
        List<Member> members = new ArrayList<>();
        Member member1 = Member.builder().email("sld1@naver.com").password("sdfsdfsdff").build();
        Member member2 = Member.builder().email("sld2@naver.com").password("sdfsdfsdff").build();
        Member member3 = Member.builder().email("sld3@naver.com").password("sdfsdfsdff").build();
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        members.add(member1);
        members.add(member2);
        members.add(member3);
        Article article = Article.builder().title("Dfdf").content("dddd").member(member1).build();
        em.persist(article);
        List<Comment> comments = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            Comment comment = Comment.builder()
                                        .title("comment " + i)
                                        .comment("content " + i)
                                        .member(members.get(i%3))
                                        .article(article)
                                        .build();
            em.persist(comment);
            comments.add(comment);
        }

        return comments;
    }



    @Test
    @DisplayName("[R] Id로 댓글 검색 테스트(댓글 O)")
    public void findByIdTest(){
        //given
        List<Comment> comments = createComment();
        em.clear();

        //when
        Optional<Comment> comment = commentRepository.findById(comments.get(0).getId());

        //then
        assertThat(comment.isPresent()).isTrue();
    }

    @Test
    @DisplayName("[R] Id로 댓글 검색 테스트(댓글 X)")
    public void findByIdFailTest(){
        //given

        //when
        Optional<Comment> comment = commentRepository.findById(10L);

        //then
        assertThat(comment.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("[R] 게시글 Id로 댓글 검색 테스트(게시글에 댓글이 있는 경우)")
    public void findAllByArticleIdTest(){
        //given
        List<Comment> comments = createComment();

        //when
        List<Comment> commentsWithArticle = commentRepository.findAllByArticleId(comments.get(0).getArticle().getId());

        //then
        assertThat(commentsWithArticle.size()).isEqualTo(10);
        assertThat(commentsWithArticle).extracting(Comment::getMember).doesNotContainNull();
        assertThat(commentsWithArticle).extracting(Comment::getMember)
                .extracting(Member::getEmail)
                .contains("sld1@naver.com", "sld2@naver.com", "sld3@naver.com");

    }

    @Test
    @DisplayName("[R] 게시글 Id로 댓글 검색 테스트(게시글에 댓글이 없는 경우)")
    public void findAllByArticleIdNoCommentTest(){
        //given


        //when
        List<Comment> commentsWithArticle = commentRepository.findAllByArticleId(10L);

        //then
        assertThat(commentsWithArticle.size()).isEqualTo(0);
    }

}
