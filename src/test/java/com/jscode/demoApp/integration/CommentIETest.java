package com.jscode.demoApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.dto.request.CommentRequest;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
public class CommentIETest {

    @Autowired
    CommentService commentService;

    @Autowired private MockMvc mvc;

    @Autowired private JwtTokenProvider tokenProvider;

    String defaultToken;
    public void createToken() {
        UserPrincipal userPrincipal = UserPrincipal.of(1L, "sdfsdf@naver.com", "{noop}123456789");
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(userPrincipal, userPrincipal.getPassword(), Set.of());
        String token = tokenProvider.createToken(authenticationToken);
        defaultToken =  JwtTokenProvider.HEADER_PREFIX + " " + token;
    }

    public CommentRequest createComment(){
        CommentRequest commentRequest = new CommentRequest();

        ReflectionTestUtils.setField(commentRequest, "title", "comment title");
        ReflectionTestUtils.setField(commentRequest, "content", "comment content");
        ReflectionTestUtils.setField(commentRequest, "articleId", 1L);

        return commentRequest;
    }

    public CommentRequest createErrorComment(){
        CommentRequest commentRequest = new CommentRequest();

        ReflectionTestUtils.setField(commentRequest, "title", "comment title");
        ReflectionTestUtils.setField(commentRequest, "content", "comment content");
        ReflectionTestUtils.setField(commentRequest, "articleId", 100L);

        return commentRequest;
    }

    public CommentRequest updatedComment(Long commentId){
        CommentRequest commentRequest = new CommentRequest();

        ReflectionTestUtils.setField(commentRequest, "id", commentId);
        ReflectionTestUtils.setField(commentRequest, "title", "updated comment title");
        ReflectionTestUtils.setField(commentRequest, "content", "updated comment content");
        ReflectionTestUtils.setField(commentRequest, "articleId", 1L);

        return commentRequest;
    }

    @Test
    @DisplayName("[POST][저장] 댓글 작성 테스트")
    public void createCommentTest() throws Exception{
        CommentRequest commentRequest = createComment();
        ObjectMapper mapper = new ObjectMapper();
        createToken();
        mvc.perform(post("/comments")
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdBy").value("sdfsdf@naver.com"));
    }

    @Test
    @DisplayName("[POST][저장] 댓글 작성 테스트(실패, 게시글 없음)")
    public void createCommentFailNoArticleTest() throws Exception{
        CommentRequest commentRequest = createErrorComment();
        ObjectMapper mapper = new ObjectMapper();
        createToken();
        mvc.perform(post("/comments")
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.COMMENT_CREATE_ERROR.getCode()));
    }

    @Test
    @DisplayName("[POST][저장] 댓글 작성 테스트(실패, 사용자 미인증)")
    public void createCommentFailNoAuthTest() throws Exception{
        CommentRequest commentRequest = createComment();
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()));
    }

    @Test
    @DisplayName("[PUT][수정] 댓글 수정 테스트")
    public void updateCommentTest() throws Exception{

        ObjectMapper mapper = new ObjectMapper();
        createToken();


        mvc.perform(put("/comments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        .content(mapper.writeValueAsString(updatedComment(1L))))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 수정되었습니다."));
    }

    @Test
    @DisplayName("[PUT][수정] 댓글 수정 테스트(실패, 사용자 미인증)")
    public void updateCommentNoAuthTest() throws Exception{

        ObjectMapper mapper = new ObjectMapper();
        createToken();


        mvc.perform(put("/comments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedComment(1L))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()));
    }

    @Test
    @DisplayName("[DELETE][삭제] 댓글 삭제 테스트")
    public void deleteCommentTest() throws Exception{

        createToken();


        mvc.perform(delete("/comments/{id}", 1L)
                                .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken)
                        )
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 삭제되었습니다."));
    }

    @Test
    @DisplayName("[DELETE][삭제] 댓글 삭제 테스트(실패, 사용자 미인증)")
    public void deleteCommentNoAuthTest() throws Exception{

        mvc.perform(delete("/comments/{id}", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()));
    }


}
