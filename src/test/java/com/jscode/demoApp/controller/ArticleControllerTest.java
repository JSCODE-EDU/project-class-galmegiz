package com.jscode.demoApp.controller;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.response.ArticleResponseDto;
import com.jscode.demoApp.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.LocalTime.now;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
    @Autowired MockMvc mvc;

    @MockBean
    ArticleService articleService;

    @Test
    @DisplayName("[GET]특정 게시글 조회 테스트(게시글 O)")
    public void getArticleTest() throws Exception {
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(new ArticleDto(articleId, "title", "content"));

        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //.andExpect(jsonPath("$..['id']").value(articleId.toString()))
                .andExpect(jsonPath("$..['title']").value("title"))
                .andExpect(jsonPath("$..['content']").value("content"));
    }

    @Test
    @DisplayName("[GET]특정 게시글 조회 테스트(게시글 X)")
    public void getArticleFailTest() throws Exception {

        given(articleService.getArticle(any(Long.class))).willThrow(new EntityNotFoundException());

        mvc.perform(get("/articles/100")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[POST]게시글 생성 테스트(정상)")
    public void createArticleTest() throws Exception{
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        given(articleService.createArticle(any(ArticleDto.class)))
                .willReturn(new ArticleDto(2L, "title", "content"));
        param.add("title", "title");
        param.add("content", "content");

        mvc.perform(post("/articles/form")
                        .params(param))
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글이 생성되었습니다."))
                .andExpect(header().string("Location", "/articles/2"))
                .andDo(print());

    }


    @DisplayName("[POST]게시글 생성 테스트(입력값 오류)")
    @MethodSource("createArticleFailTest")
    @ParameterizedTest(name = "[{index}] message : {2}")
    public void createArticleFailTest(String title, String content, String message, int numOfErrors) throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("title", title);
        param.add("content", content);

        mvc.perform(post("/articles/form")
                    .params(param))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasSize(numOfErrors)))
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
