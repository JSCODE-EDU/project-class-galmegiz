package com.jscode.demoApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.service.ArticleService;
import com.jscode.demoApp.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
public class ArticleIETest {
    @Autowired private MockMvc mvc;

    @Autowired private ArticleService articleService;
    @Autowired private MemberService memberService;
    @Autowired private JwtTokenProvider tokenProvider;
    String defaultToken;

    /*
    public void createArticleWithComment(){
        MemberDto memberDto = new MemberDto()
    }
*/
    public void createToken() {
        UserPrincipal userPrincipal = UserPrincipal.of(1L, "sdfsdf@naver.com", "{noop}123456789");
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(userPrincipal, userPrincipal.getPassword(), Set.of());
        String token = tokenProvider.createToken(authenticationToken);
        defaultToken =  JwtTokenProvider.HEADER_PREFIX + " " + token;
    }

    public String createToken(Long id, String email){
        UserPrincipal userPrincipal = UserPrincipal.of(id, email, "{noop}123456789");
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(userPrincipal, userPrincipal.getPassword(), Set.of());
        String token = tokenProvider.createToken(authenticationToken);
        return JwtTokenProvider.HEADER_PREFIX + " " + token;
    }

    @DisplayName("[GET][조회] 게시물 ID 조회 API 테스트(성공)")
    @Test()
    public void getArticleByIdTest() throws Exception {
        //given
        mvc.perform(RestDocumentationRequestBuilders.get("/articles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("제목 1"))
                .andExpect(jsonPath("$.content").value("내용 1"))
                .andExpect(jsonPath("$.createdBy").exists())
                .andExpect(jsonPath("$.commentResponses").isArray())
                .andExpect(jsonPath("$.commentResponses", hasSize(3)))
                .andDo(print())
                .andDo(document("getArticle",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                        fieldWithPath("createdBy").type(JsonFieldType.STRING).description("게시글 작성자"),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성일"),
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                        fieldWithPath("commentResponses").type(JsonFieldType.ARRAY).description("게시글 댓글 목록"),
                                        fieldWithPath("commentResponses.[].id").type(JsonFieldType.NUMBER).description("댓글 Id"),
                                        fieldWithPath("commentResponses.[].title").type(JsonFieldType.STRING).description("댓글 제목"),
                                        fieldWithPath("commentResponses.[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("commentResponses.[].createdBy").type(JsonFieldType.STRING).description("댓글 작성자"),
                                        fieldWithPath("commentResponses.[].createdAt").type(JsonFieldType.STRING).description("댓글 생성일"),
                                        fieldWithPath("commentResponses.[].modifiedAt").type(JsonFieldType.STRING).description("댓글 수정일")


                                )
                        ));
    }

    @DisplayName("[GET][조회] 게시물 ID 조회 API 테스트(실패, 게시글 없음)")
    @Test()
    public void getArticleByIdFailTest() throws Exception {
        //given
        mvc.perform(RestDocumentationRequestBuilders.get("/articles/{id}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
                .andDo(print())
                .andDo(document("getArticleFail(NoArticle)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                responseFields(
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("A").value("게시글"), key("S").value("서비스 레이어"), key("001").value("에러번호")),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                    fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                                )

                        ));
    }


    @DisplayName("[POST][저장] 게시글 작성 API 테스트")
    @Test
    public void postArticleTest() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "제목");
        params.add("content", "내용");
        createToken();

        mvc.perform(post("/articles/form")
                        .params(params)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글이 생성되었습니다."))
                .andExpect(header().exists("Location"))
                .andDo(print())
                .andDo(document("postArticle",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                requestHeaders(headerWithName("Authorization").description("JWT 인증 헤더 필드, Bearer 접두사 필요")),
                                requestParameters(
                                        parameterWithName("title").description("제목").attributes(key("constraints").value("10자 이상")),
                                        parameterWithName("content").description("내용").attributes(key("constraints").value("ddd"))
                        )
                ));
    }

    @DisplayName("[POST][저장] 게시글 작성 API 테스트(실패, 사용자인증 실패)")
    @Test
    public void postArticleFailNoAuthTest() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "제목");
        params.add("content", "내용");


        mvc.perform(post("/articles/form")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(print())
                .andDo(document("postArticleFail(NoAuth)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("G").value("Global"), key("A").value("모든 계층"), key("001").value("에러번호")),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                        )
                        ));
    }

    @DisplayName("[PUT][수정] 게시글 수정 API 테스트")
    @Test
    public void updateArticleTest() throws  Exception{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        params.put("title", "수정된 제목");
        params.put("content", "수정된 내용");
        createToken();

        mvc.perform(put("/articles/{id}", 1L)
                        .content(mapper.writeValueAsString(params))
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andDo(document("updateArticle",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                requestHeaders(headerWithName("Authorization").description("JWT 인증 헤더 필드, Bearer 접두사 필요")),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("수정할 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 내용"),
                                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정한 날짜"),
                                        fieldWithPath("modifiedBy").type(JsonFieldType.STRING).description("수정한 사람")
                                )
                        ));
    }

    @DisplayName("[PUT][수정] 게시글 수정 API 테스트(실패, 사용자 인증 실패)")
    @Test
    public void updateArticleFailNoAuthTest() throws  Exception{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        params.put("title", "수정된 제목");
        params.put("content", "수정된 내용");


        mvc.perform(put("/articles/{id}", 1L)
                        .content(mapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(document("updateArticle(NoAuth)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("G").value("Global"), key("A").value("모든 계층"), key("002").value("에러번호")),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                        fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                                )
                        ));
    }

    @DisplayName("[PUT][수정] 게시글 수정 API 테스트(실패, 게시글 없음)")
    @Test
    public void updateArticleFailNoArticleTest() throws  Exception{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("id", "100");
        params.put("title", "수정된 제목");
        params.put("content", "수정된 내용");
        createToken();


        mvc.perform(put("/articles/{id}", 100L)
                        .content(mapper.writeValueAsString(params))
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
                .andDo(document("updateArticle(NoArticle)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("A").value("게시글"), key("S").value("서비스 계층"), key("001").value("에러번호")),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                        fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                                )
                        ));
    }

    @DisplayName("[PUT][수정] 게시글 수정 API 테스트(실패, 게시글 수정 권한 없음)")
    @Test
    public void updateArticleFailNoAuthToArticleTest() throws  Exception{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        params.put("title", "수정된 제목");
        params.put("content", "수정된 내용");



        mvc.perform(put("/articles/{id}", 1L)
                        .content(mapper.writeValueAsString(params))
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, createToken(100L, "noauth@naver.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(document("updateArticle(NoAuthToComment)",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                        responseFields(
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("A").value("게시글"), key("S").value("서비스 계층"), key("001").value("에러번호")),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                                fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                                        )
                        ));
    }

    @DisplayName("[DELETE][삭제] 게시글 삭제 API 테스트")
    @Test
    public void deleteArticleTest() throws  Exception{
        createToken();

        mvc.perform(RestDocumentationRequestBuilders.delete("/articles/{id}", 1L)
                .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken))
                .andExpect(status().isOk())
                .andExpect(content().string("게시물이 삭제되었습니다."))
                .andDo(document("deleteArticle",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                requestHeaders(headerWithName("Authorization").description("JWT 인증 헤더 필드, Bearer 접두사 필요")),
                                pathParameters(parameterWithName("id").description("삭제할 게시글 id"))
                        ));
    }

    @DisplayName("[DELETE][삭제] 게시글 삭제 API 테스트(실패, 사용자 인증 실패)")
    @Test
    public void deleteArticleFailNoAuthTest() throws  Exception{


        mvc.perform(delete("/articles/{id}", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(document("deleteArticleFail(NoAuth)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("A").value("게시글"), key("S").value("서비스 계층"), key("001").value("에러번호")),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                        fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                                )
                        ));
    }

    @DisplayName("[DELETE][삭제] 게시글 삭제 API 테스트(실패, 게시물 삭제 권한 없음)")
    @Test
    public void deleteArticleFailNoAuthToArticleTest() throws  Exception{


        mvc.perform(delete("/articles/{id}", 1L)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, createToken(100L, "test@naver.com")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(document("deleteArticle(NoAuthToComment)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드").attributes(key("A").value("게시글"), key("S").value("서비스 계층"), key("001").value("에러번호")),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                        fieldWithPath("messageDetail").type(JsonFieldType.STRING).description("에러 메시지 상세").optional()
                                )
                        ));
    }

    @DisplayName("[GET] 게시글 검색 API 테스트(제목 검색)")
    @Test
    public void searchArticleTest() throws  Exception{
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("SearchType", "TITLE");
        param.add("SearchKeyword", "제목 1");

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andDo(print())
                .andDo(document("searchArticle",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                requestParameters(
                                        parameterWithName("SearchType").description("검색 조건(제목)").optional(),
                                        parameterWithName("SearchKeyword").description("검색 내용")
                                ),
                                responseFields(
                                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("검색 게시글 목록"),
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("검색된 게시글 ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("검색된 게시글 제목"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("검색된 게시글 내용"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("검색된 게시글 작성일"),
                                        fieldWithPath("[].createdBy").type(JsonFieldType.STRING).description("검색된 게시글 작성자")
                                )

                        ));
    }

    @DisplayName("[GET] 게시글 검색 API 테스트(검색 keyword 없음)")
    @Test
    public void searchArticleNoKeywordTest() throws  Exception{
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("SearchType", "");
        param.add("SearchKeyword", "제목 1");

        mvc.perform(get("/articles")
                        .queryParams(param))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print())
                .andDo(document("searchArticle(NoKeyword)",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)"))
                        ));
    }

    @DisplayName("[POST] 게시글 좋아요 기능 테스트")
    @Test
    public void likeArticleTest() throws Exception {
        createToken();
        mvc.perform(RestDocumentationRequestBuilders.post("/articles/{id}/like", 1L)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken) )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("articleLike",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                                requestHeaders(headerWithName("Authorization").description("JWT 인증 헤더 필드, Bearer 접두사 필요")),
                                pathParameters(parameterWithName("id").description("좋아요 할 게시글 id"))
                        ));
    }

    @DisplayName("[POST] 게시글 좋아요 취소 기능 테스트")
    @Test
    public void likeArticleCancelTest() throws Exception {
        createToken();
        mvc.perform(post("/articles/{id}/like", 1L)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken) )
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요 개수가 1되었습니다."));
        mvc.perform(RestDocumentationRequestBuilders.post("/articles/{id}/like", 1L)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken) )
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요 개수가 -1되었습니다."))
                .andDo(document("articleDislike",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint(), removeMatchingHeaders("(X-.*)|(Vary)")),
                        requestHeaders(headerWithName("Authorization").description("JWT 인증 헤더 필드, Bearer 접두사 필요")),
                        pathParameters(parameterWithName("id").description("좋아요 취소 게시글 id"))
                        ));
    }

}
