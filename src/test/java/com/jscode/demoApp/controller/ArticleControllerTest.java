package com.jscode.demoApp.controller;

import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.ArticleMemberLike;
import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.PageRequest;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.service.ArticleService;
import com.jscode.demoApp.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//SecurityConfig는 JwtTokenProvider에 대한 추가 의존 필요, 단순 조회 시 해당 기능 불필요하여 테스트 구분
@WebMvcTest(ArticleController.class)
@ActiveProfiles("test")
@Import({SearchValidator.class})
@AutoConfigureMockMvc(addFilters = false)
public class ArticleControllerTest {
    @Autowired MockMvc mvc;

    @MockBean
    ArticleService articleService;
    @MockBean
    LikeService likeService;

    PageRequest defaultPageRequest;
    {
        defaultPageRequest = new PageRequest(0, 100);
    }

    @Test
    @DisplayName("[GET]게시글 Id 이용 조회 테스트(게시글 O)")
    public void getArticleTest() throws Exception {
        Long articleId = 1L;
        ArticleDto articleDto = createArticleDtoWithMember();

        given(articleService.getArticle(articleId)).willReturn(articleDto);

        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //.andExpect(jsonPath("$..['id']").value(articleId.toString()))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(print());
    }

    public ArticleDto createArticleDtoWithMember(){
        Member member = Member.builder().id(1L).email("som@naver.com").password("123456789").build();
        Article article = Article.builder().title("title").content("content").member(member).build();

        Comment sampleComment1 = Comment.builder().article(article).member(member).title("comment t 1").comment("comment c 1").build();
        Comment sampleComment2 = Comment.builder().article(article).member(member).title("comment t 1").comment("comment c 1").build();

        ArticleMemberLike sampleLike1 = ArticleMemberLike.of(article, member);
        ArticleMemberLike sampleLike2 = ArticleMemberLike.of(article, member);





        ReflectionTestUtils.setField(article, "id", 1L);
        /* 편의 메소드 작성했기 때문에 add 해주면 안 됨. 실수 주의 필요
        List<Comment> comments = (ArrayList<Comment>) ReflectionTestUtils.getField(article, "comments");
        comments.add(sampleComment1);
        comments.add(sampleComment2);
        */


        return ArticleDto.fromEntity(article);
    }

    @Test
    @DisplayName("[GET]게시글 Id 이용 조회 테스트(게시글 X)")
    public void getArticleFailTest() throws Exception {

        given(articleService.getArticle(any(Long.class))).willThrow(new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        mvc.perform(get("/articles/100")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("[GET]게시글 검색 테스트(SearchType Null)")
    public void getArticleByNoneTest() throws Exception {
        SearchRequestDto dto = new SearchRequestDto(null, null);
        given(articleService.searchArticle(dto, defaultPageRequest)).willReturn(new ArrayList<ArticleDto>());

        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[GET]게시글 유효하지 않은 검색 테스트(Invalid SearchType1)")
    public void getArticleByInvalidSearchType() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("searchType", "CONTENT");
        param.add("searchKeyword", "");


        given(articleService.searchArticle(any(SearchRequestDto.class), any(PageRequest.class))).willReturn(new ArrayList<ArticleDto>());

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("[GET]게시글 유효하지 않은 검색 테스트(Invalid SearchType2)")
    public void getArticleByInvalidSearchTypeAndShort() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("searchType", "TITLE1");
        param.add("searchKeyword", "aaa");

        given(articleService.searchArticle(any(SearchRequestDto.class), any(PageRequest.class))).willReturn(new ArrayList<ArticleDto>());

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andDo(print());
    }

    @Test
    @DisplayName("[GET]게시글 제목 검색 테스트(SearchType TITLE)")
    public void getArticleByTitleTest() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("searchType", "TITLE");
        param.add("searchKeyword", "wow");
        given(articleService.searchArticle(any(SearchRequestDto.class), any(PageRequest.class))).willReturn(new ArrayList<ArticleDto>());

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[GET]게시글 제목 검색 실패 테스트(SearchType TITLE)")
    public void getArticleByTitleLengthFailTest() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("searchType", "TITLE");
        param.add("searchKeyword", "");
        given(articleService.searchArticle(any(SearchRequestDto.class), any(PageRequest.class))).willReturn(new ArrayList<ArticleDto>());

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }



/*
    @Test
    @DisplayName("게시물 전체 조회 컨트롤러 테스트")
    public void getAllArticlesTest() throws Exception {
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(model().attributeExists("articleDtos"))
                .andExpect(view().name("articles/index"))
                .andDo(print());
    }
    */
}
