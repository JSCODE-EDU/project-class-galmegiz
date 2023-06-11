package com.jscode.demoApp.service;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.PageRequest;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.AuthorizeException;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceUnitTest {
    @InjectMocks private ArticleService articleService;
    @Mock private ArticleRepository articleRepository;
    @Mock private MemberService memberService;

    private PageRequest defaultPageRequest;

    {
        defaultPageRequest = new PageRequest(0, 100);
    }

    @Test
    @DisplayName("[검색]id로 게시글 찾기 테스트(실패)")
    public void findByIdFailTest(){
        //given
        Long id = 1L;
        given(articleRepository.findById(id)).willReturn(Optional.empty());

        //when then
        Assertions.assertThatThrownBy(() -> articleService.getArticle(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("[저장]저장 테스트")
    public void saveTest(){
        //given
        Long userId = 1L;
        ArticleDto articleDto = ArticleDto.of(null, "제목", "내용");


        given(articleRepository.save(any(Article.class))).willReturn(articleDto.toEntity());
        given(memberService.findMemberById(userId)).willReturn(MemberDto.of("ss", "SS"));

        //when then
        articleService.createArticle(articleDto, userId);
        then(articleRepository).should().save(any(Article.class));
    }

    @ParameterizedTest(name = "[{index}] message : {0}({1})")
    @MethodSource("searchByTitleTest")
    @DisplayName("[검색]검색 테스트")
    public void searchByTest(String message, SearchType searchType, String searchKeyword){
        SearchRequestDto param = new SearchRequestDto(searchType, searchKeyword);

        if(searchType == null){
            given(articleRepository.findAll(defaultPageRequest)).willReturn(new ArrayList<Article>());

            articleService.searchArticle(param, defaultPageRequest);

            then(articleRepository).should().findAll(defaultPageRequest);

        } else if (searchType == SearchType.TITLE) {
            given(articleRepository.findByTitle(searchKeyword, defaultPageRequest)).willReturn(new ArrayList<Article>());

            List<ArticleDto> articleDtos = articleService.searchArticle(param, defaultPageRequest);

            then(articleRepository).should().findByTitle(searchKeyword, defaultPageRequest);
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

    @Test
    @DisplayName("[삭제] 게시글 삭제 성공 테스트")
    public void articleDeleteTest(){
        //given
        Member member = Member.builder().id(1L).email("Sss").password("ddd").build();
        Article article = Article.builder().title("sss").content("ddd").member(member).build();
        Long articleId = 1L;
        Long userId = 1L;

        willDoNothing().given(articleRepository).delete(any(Article.class));
        given(articleRepository.findById(1L)).willReturn(Optional.of(article));

        //when
        articleService.deleteArticle(articleId, userId);

        //then

    }

    @Test
    @DisplayName("[삭제] 게시글 삭제 실패 테스트[게시글 없음]")
    public void articleDeleteFailNoArticleTest(){
        //given
        Long articleId = 1L;
        Long userId = 1L;

        given(articleRepository.findById(1L)).willThrow(new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND));


        //when then
        Assertions.assertThatThrownBy(() -> articleService.deleteArticle(articleId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(articleRepository, never()).delete(any(Article.class));

    }

    @Test
    @DisplayName("[삭제] 게시글 삭제 실패 테스트[게시글 작성자 불일치]")
    public void articleDeleteFailUserMismatchTest(){
        //given
        Member member = Member.builder().id(2L).email("Sss").password("ddd").build();
        Article article = Article.builder().title("sss").content("ddd").member(member).build();
        Long articleId = 1L;
        Long userId = 1L;

        given(articleRepository.findById(1L)).willReturn(Optional.of(article));


        //when then
        Assertions.assertThatThrownBy(() -> articleService.deleteArticle(articleId, userId))
                .isInstanceOf(AuthorizeException.class);
        verify(articleRepository, never()).delete(any(Article.class));

    }

    @Test
    @DisplayName("[수정] 게시글 수정 성공 테스트")
    public void articleUpdateTest(){
        //given
        ArticleDto articleDto = ArticleDto.of(1L, "수정 제목", "수정 내용");
        Long userId = 1L;
        Member member = Member.builder().id(userId).email("sss").password("ddd").build();
        Article original = Article.builder().title("수정전 제목").content("수정전 내용").member(member).build();
        given(articleRepository.findById(articleDto.getId())).willReturn(Optional.of(original));

        //when
        ArticleDto updatedArticleDto = articleService.updateArticle(articleDto, userId);

        //then
        Assertions.assertThat(updatedArticleDto.getTitle()).isEqualTo(articleDto.getTitle());
        Assertions.assertThat(updatedArticleDto.getContent()).isEqualTo(articleDto.getContent());
        Assertions.assertThat(updatedArticleDto.getMember().getId()).isEqualTo(userId);

    }

    @Test
    @DisplayName("[수정] 게시글 수정 실패 테스트[게시글 없음]")
    public void articleUpdateFailNoArticleTest(){
        //given
        ArticleDto articleDto = ArticleDto.of(1L, "title", "content");
        Long userId = 1L;
        given(articleRepository.findById(1L)).willThrow(new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        //when then
        Assertions.assertThatThrownBy(() -> articleService.updateArticle(articleDto, userId))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    @DisplayName("[수정] 게시글 수정 실패 테스트[게시글 작성자 불일치]")
    public void articleUpdateFailUserMismatchTest(){
        //given
        ArticleDto articleDto = ArticleDto.of(1L, "수정 제목", "수정 내용");

        Member member = Member.builder().id(2L).email("Sss").password("ddd").build();
        Article article = Article.builder().title("sss").content("ddd").member(member).build();
        Long articleId = 1L;
        Long userId = 1L;

        given(articleRepository.findById(1L)).willReturn(Optional.of(article));


        //when

        Assertions.assertThatThrownBy(() -> articleService.updateArticle(articleDto, userId))
                .isInstanceOf(AuthorizeException.class);
    }



}
