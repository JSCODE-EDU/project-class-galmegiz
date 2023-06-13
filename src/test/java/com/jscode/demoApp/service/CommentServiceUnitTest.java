package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.CommentDto;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.error.exception.ResourceCreationException;
import com.jscode.demoApp.repository.ArticleRepository;
import com.jscode.demoApp.repository.CommentRepository;
import com.jscode.demoApp.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private MemberRepository memberRepository;

    @DisplayName("[저장] 댓글 저장 테스트(성공)")
    @Test
    public void commentSaveTest(){
        //given
        MemberDto memberDto = MemberDto.of(1L, "email", "password", null);
        CommentDto commentDto = CommentDto.of(1L, "title", "content", 1L, memberDto, null, null);
        Member member = Member.builder().id(1L).email("email").password("password").build();
        Article article = Article.builder().title("title").content("content").member(member).build();
        given(articleRepository.getReferenceById(commentDto.getId())).willReturn(Optional.of(article));
        given(memberRepository.getReferenceById(commentDto.getMemberDto().getId())).willReturn(Optional.of(member));
        given(commentRepository.save(any(Comment.class))).willReturn(any(Comment.class));

        //when
        commentService.createComment(commentDto);

        //then
        verify(commentRepository).save(any(Comment.class));
    }

    @DisplayName("[저장] 댓글 저장 테스트(실패, 게시글 없음)")
    @Test
    public void commentSaveFailNoArticleTest(){
        //given
        MemberDto memberDto = MemberDto.of(1L, "email", "password", null);
        CommentDto commentDto = CommentDto.of(1L, "title", "content", 1L, memberDto, null, null);
        Member member = Member.builder().id(1L).email("email").password("password").build();
        Article article = Article.builder().title("title").content("content").member(member).build();
        given(articleRepository.getReferenceById(commentDto.getId())).willReturn(Optional.empty());
        given(memberRepository.getReferenceById(commentDto.getMemberDto().getId())).willReturn(Optional.of(member));


        //when then
        Assertions.assertThatThrownBy(() -> commentService.createComment(commentDto))
                .isInstanceOf(ResourceCreationException.class);

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @DisplayName("[저장] 댓글 저장 테스트(실패, 사용자 없음)")
    @Test
    public void commentSaveFailNoMemberTest(){
        //given
        MemberDto memberDto = MemberDto.of(1L, "email", "password", null);
        CommentDto commentDto = CommentDto.of(1L, "title", "content", 1L, memberDto, null, null);
        Member member = Member.builder().id(1L).email("email").password("password").build();
        Article article = Article.builder().title("title").content("content").member(member).build();
        given(articleRepository.getReferenceById(commentDto.getId())).willReturn(Optional.of(article));
        given(memberRepository.getReferenceById(commentDto.getMemberDto().getId())).willReturn(Optional.empty());


        //when then
        Assertions.assertThatThrownBy(() -> commentService.createComment(commentDto))
                .isInstanceOf(ResourceCreationException.class);

        verify(commentRepository, never()).save(any(Comment.class));
    }
    /*
    @DisplayName("[수정] 댓글 수정 테스트(성공)")
    @Test
    public void commentUpdateTest(){

    }

    @DisplayName("[수정] 댓글 수정 테스트(실패, 댓글 작성자 불일치)")
    @Test
    public void commentUpdateFailNoAuthTest(){

    }

    @DisplayName("[수정] 댓글 수정 테스트(실패, 댓글 미존재)")
    @Test
    public void commentUpdateFailNoCommentTest(){

    }

    @DisplayName("[삭제] 댓글 삭제 테스트(성공)")
    @Test
    public void commentDeleteTest(){

    }

    @DisplayName("[삭제] 댓글 삭제 테스트(실패, 댓글 작성자 불일치")
    @Test
    public void commentDeleteFailNoAuthTest(){

    }

    @DisplayName("[삭제] 댓글 삭제 테스트(실패, 댓글 미존재)")
    @Test
    public void commentDeleteTestNoComment(){

    }
    */
}
