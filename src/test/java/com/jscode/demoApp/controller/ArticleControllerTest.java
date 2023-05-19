package com.jscode.demoApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.dto.response.ArticleResponseDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.service.ArticleService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;

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
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@Import(SearchValidator.class)
public class ArticleControllerTest {
    @Autowired MockMvc mvc;

    @MockBean
    ArticleService articleService;

    @Test
    @DisplayName("[GET]게시글 Id 이용 조회 테스트(게시글 O)")
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
    @DisplayName("[GET]게시글 Id 이용 조회 테스트(게시글 X)")
    public void getArticleFailTest() throws Exception {

        given(articleService.getArticle(any(Long.class))).willThrow(new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        mvc.perform(get("/articles/100")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[GET]게시글 검색 테스트(SearchType Null)")
    public void getArticleByNoneTest() throws Exception {
        SearchRequestDto dto = new SearchRequestDto(null, null);
        given(articleService.searchArticle(dto)).willReturn(new ArrayList<ArticleDto>());

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


        given(articleService.searchArticle(any(SearchRequestDto.class))).willReturn(new ArrayList<ArticleDto>());

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

        given(articleService.searchArticle(any(SearchRequestDto.class))).willReturn(new ArrayList<ArticleDto>());

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
        given(articleService.searchArticle(any(SearchRequestDto.class))).willReturn(new ArrayList<ArticleDto>());

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
        given(articleService.searchArticle(any(SearchRequestDto.class))).willReturn(new ArrayList<ArticleDto>());

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isBadRequest())
                .andDo(print());
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

    @DisplayName("[DELETE]게시글 삭제 테스트(게시글 O)")
    @Test
    public void deleteArticleTest() throws Exception {
        willDoNothing().given(articleService).deleteArticle(any(Long.class));

        mvc.perform(delete("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("게시물이 삭제되었습니다."));

    }

    @DisplayName("[DELETE]게시물 삭제 테스트(게시글 X)")
    @Test
    public void deleteArticleFailTest() throws Exception{
        willThrow(new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND)).given(articleService).deleteArticle(any(Long.class));

        mvc.perform(delete("/articles/1"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[PUT]게시물 수정 테스트(게시글 O)")
    @Test
    public void updateArticleTest() throws Exception{
        //request를 mocking할 순 없을가?
        ObjectMapper request = new ObjectMapper();
        Map<String, String> param = new HashMap<>();
        param.put("id", "1");
        param.put("title", "titl");
        param.put("content", "content");
        given(articleService.updateArticle(any(ArticleDto.class))).willReturn(new ArticleDto(2L, "수정 제목", "수정 내용"));

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.writeValueAsString(param)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("[PUT]게시물 수정 테스트(게시글 X)")
    @Test
    public void updateArticleFailTest() throws Exception{
        ObjectMapper request = new ObjectMapper();
        Map<String, String> param = new HashMap<>();
        param.put("id", "1");
        param.put("title", "titl");
        param.put("content", "content");


        given(articleService.updateArticle(any(ArticleDto.class))).willThrow(new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

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


        given(articleService.updateArticle(any(ArticleDto.class))).willThrow(new EntityNotFoundException());

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.writeValueAsString(param)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("A-C-001"))
                .andDo(print());
    }

    @DisplayName("[Common] DataBindingError 테스트 ")
    @Test()
    void requestBodyBindingErrorTest() throws Exception {
        given(articleService.updateArticle(any(ArticleDto.class))).willReturn(null);

        mvc.perform(put("/articles/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("A-C-001"))
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
