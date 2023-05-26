package com.jscode.demoApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
public class RestTest {
    @Autowired private MockMvc mvc;

    @Autowired private ArticleService articleService;


    @DisplayName("[GET] 게시물 ID 조회 API 테스트")
    @Test()
    public void getArticleByIdTest() throws Exception {
        //given
        ArticleDto articleDto = articleService.createArticle(new ArticleDto(null, "제목", "내용"));



        mvc.perform(RestDocumentationRequestBuilders.get("/articles/{id}", articleDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..['title']").value("제목"))
                .andExpect(jsonPath("$..['content']").value("내용"))
                .andDo(print())
                .andDo(document("GET-getArticle",
                                    pathParameters(
                                            parameterWithName("id").description("게시글 id")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("게시글 id"),
                                            fieldWithPath("title").description("게시글 제목"),
                                            fieldWithPath("content").description("게시글 내용"),
                                            fieldWithPath("createdAt").description("게시글 생성일")
                                    )
                                )
                );
    }

    @DisplayName("[POST] 게시글 작성 API 테스트")
    @Test
    public void postArticleTest() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "제목");
        params.add("content", "내용");

        mvc.perform(post("/articles/form")
                    .params(params)
                    .contentType(MediaType.TEXT_PLAIN)
                        )
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글이 생성되었습니다."))
                .andExpect(header().exists("Location"))
                .andDo(print())
                .andDo(document("POST-createArticle"));
    }

    @DisplayName("[PUT] 게시글 수정 API 테스트")
    @Test
    public void updateArticleTest() throws  Exception{
        ObjectMapper mapper = new ObjectMapper();
        ArticleDto articleDto = articleService.createArticle(new ArticleDto(null, "제목", "내용"));
        Map<String, String> params = new HashMap<>();
        params.put("id", articleDto.getId().toString());
        params.put("title", "수정된 제목");
        params.put("content", "수정된 내용");

        mvc.perform(put("/articles/{id}", articleDto.getId())
                        .content(mapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("PUT-updateArticle"));
    }

    @DisplayName("[DELETE] 게시글 삭제 API 테스트")
    @Test
    public void deleteArticleTest() throws  Exception{
        ArticleDto articleDto = articleService.createArticle(new ArticleDto(null, "제목", "내용"));

        mvc.perform(delete("/articles/{id}", articleDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("게시물이 삭제되었습니다."))
                .andDo(document("DELETE-deleteArticle"));
    }

    @DisplayName("[GET] 게시글 검색 API 테스트")
    @Test
    public void searchArticleTest() throws  Exception{
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        articleService.createArticle(new ArticleDto(null, "제목", "내용"));
        param.add("SearchType", "TITLE");
        param.add("SearchKeyword", "제목");
        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("GET-searchArticle"));
    }

}
