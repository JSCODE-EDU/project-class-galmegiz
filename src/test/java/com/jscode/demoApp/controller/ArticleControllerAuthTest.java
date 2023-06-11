package com.jscode.demoApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.ArticleMemberLike;
import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.PageRequest;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.service.ArticleService;
import com.jscode.demoApp.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ArticleController.class)
@ActiveProfiles("test")
@Import({SearchValidator.class, TestSecurityConfig.class})
public class ArticleControllerAuthTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ArticleService articleService;
    @MockBean
    LikeService likeService;

    PageRequest defaultPageRequest;
    {
        defaultPageRequest = new PageRequest(0, 100);
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

    public ArticleDto updateArticleDtoWithMember(String title, String content){
        Member member = Member.builder().id(1L).email("som@naver.com").password("123456789").build();
        Article article = Article.builder().title(title).content(content).member(member).build();

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
    @WithUserDetails(userDetailsServiceBeanName = "testDetailService")
    @Test
    @DisplayName("[POST]게시글 생성 테스트(정상)")
    public void createArticleTest() throws Exception{
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        given(articleService.createArticle(any(ArticleDto.class), anyLong()))
                .willReturn(createArticleDtoWithMember());
        param.add("title", "title");
        param.add("content", "content");

        mvc.perform(post("/articles/form")
                        .params(param))
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글이 생성되었습니다."))
                .andExpect(header().string("Location", "/articles/1"))
                .andDo(print());

    }


    @Test
    @DisplayName("[POST]게시글 생성 테스트(실패, 미인증)")
    public void createArticleFailTestWithoutUser() throws Exception{
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        given(articleService.createArticle(any(ArticleDto.class), anyLong()))
                .willReturn(createArticleDtoWithMember());
        param.add("title", "title");
        param.add("content", "content");

        mvc.perform(post("/articles/form")
                        .params(param))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(print());

    }


    @DisplayName("[POST]게시글 생성 테스트(실패, 입력값 오류)")
    @MethodSource("createArticleFailTest")
    @ParameterizedTest(name = "[{index}] message : {2}")
    public void createArticleFailTest(String title, String content, String message, int numOfErrors) throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("title", title);
        param.add("content", content);
        //Todo : 에러 필드 구체적으로 검사하도록 테스트코드 수정 필요
        mvc.perform(post("/articles/form")
                        .params(param))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andDo(print());

    }

    static Stream<Arguments> createArticleFailTest(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i <= 1000; i++){
            sb.append("a");
        }
        String largeContent = sb.toString();
        return Stream.of(
                arguments(null, null, "입력값 없음", 2),
                arguments("title", null, "내용 없음", 1),
                arguments(null, "content", "제목 없음", 1),
                arguments(" ", "content", "제목 공백", 1),
                arguments(" ", largeContent, "제목 공백, 내용 1000자 초과", 2),
                arguments("title", largeContent, "내용 1000자 초과", 1)
        );
    }

    @WithUserDetails(userDetailsServiceBeanName = "testDetailService")
    @DisplayName("[DELETE]게시글 삭제 테스트(게시글 O)")
    @Test
    public void deleteArticleTest() throws Exception {
        willDoNothing().given(articleService).deleteArticle(any(Long.class), any(Long.class));

        mvc.perform(delete("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("게시물이 삭제되었습니다."));

    }


    @DisplayName("[DELETE]게시글 삭제 테스트(실패, 미인증)")
    @Test
    public void deleteArticleFailNoAuthTest() throws Exception {
        willDoNothing().given(articleService).deleteArticle(any(Long.class), any(Long.class));

        mvc.perform(delete("/articles/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()));

    }

    @WithUserDetails
    @DisplayName("[DELETE]게시물 삭제 테스트(실패, 게시글 X)")
    @Test
    public void deleteArticleFailTest() throws Exception{
        willThrow(new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND)).given(articleService).deleteArticle(any(Long.class), any(Long.class));

        mvc.perform(delete("/articles/1"))
                .andExpect(status().isNotFound());
    }

    @WithUserDetails
    @DisplayName("[PUT]게시물 수정 테스트(게시글 O)")
    @Test
    public void updateArticleTest() throws Exception{
        //request를 mocking할 순 없을가?
        ObjectMapper request = new ObjectMapper();
        Map<String, String> param = new HashMap<>();
        param.put("id", "1");
        String updatedTitle = "수정 제목";
        String updatedContent = "수정 내용";
        param.put("title", updatedTitle);
        param.put("content", updatedContent);
        given(articleService.updateArticle(any(ArticleDto.class), any(Long.class))).willReturn(updateArticleDtoWithMember(updatedTitle, updatedContent));

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.writeValueAsString(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.content").value(updatedContent))
                .andDo(print());
    }


    @DisplayName("[PUT]게시물 수정 테스트(실패, 미인증)")
    @Test
    public void updateArticleFailTestNoAuth() throws Exception{
        //request를 mocking할 순 없을가?
        ObjectMapper request = new ObjectMapper();
        Map<String, String> param = new HashMap<>();
        param.put("id", "1");
        param.put("title", "titl");
        param.put("content", "content");
        given(articleService.updateArticle(any(ArticleDto.class), any(Long.class))).willReturn(createArticleDtoWithMember());

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.writeValueAsString(param)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()));
    }

    @WithUserDetails
    @DisplayName("[PUT]게시물 수정 테스트(실패, 게시글 X)")
    @Test
    public void updateArticleFailTest() throws Exception{
        ObjectMapper request = new ObjectMapper();
        Map<String, String> param = new HashMap<>();
        param.put("id", "1");
        param.put("title", "titl");
        param.put("content", "content");


        given(articleService.updateArticle(any(ArticleDto.class), any(Long.class))).willThrow(new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.writeValueAsString(param)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("[PUT]게시물 수정 테스트(게시글 ID에 String 입력)")
    @Test
    public void updateArticleWrongIdTypeFailTest() throws Exception{
        ObjectMapper request = new ObjectMapper();
        Map<String, String> param = new HashMap<>();
        param.put("id", "string");
        param.put("title", "titl");
        param.put("content", "content");


        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.writeValueAsString(param)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.REQUEST_FIELD_ERROR.getCode()))
                .andDo(print());
    }

    @DisplayName("[Common] DataBindingError 테스트 ")
    @Test()
    void requestBodyBindingErrorTest() throws Exception {
        given(articleService.updateArticle(any(ArticleDto.class), any(Long.class))).willReturn(null);

        mvc.perform(put("/articles/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.REQUEST_FIELD_ERROR.getCode()))
                .andDo(print());
    }

    @DisplayName("[Common] Global Error 처리 테스트 ")
    @Test()
    void globalErrorTest() throws Exception {
        given(articleService.getArticle(any(Long.class))).willThrow(new RuntimeException());

        mvc.perform(get("/articles/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.SEVER_GLOBAL_ERROR.getCode()))
                .andDo(print());
    }
}
