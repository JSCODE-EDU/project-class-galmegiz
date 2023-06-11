package com.jscode.demoApp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.CommentDto;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.CommentRequest;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class CommentControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CommentService commentService;

    public String createRequest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CommentRequest commentRequest = new CommentRequest();

        ReflectionTestUtils.setField(commentRequest, "title", "comment title");
        ReflectionTestUtils.setField(commentRequest, "content", "comment content");
        ReflectionTestUtils.setField(commentRequest, "articleId", 1L);

        return mapper.writeValueAsString(commentRequest);
    }

    public String createUpdateRequest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CommentRequest commentRequest = new CommentRequest();

        ReflectionTestUtils.setField(commentRequest, "id", 1L);
        ReflectionTestUtils.setField(commentRequest, "title", "comment title");
        ReflectionTestUtils.setField(commentRequest, "content", "comment content");
        ReflectionTestUtils.setField(commentRequest, "articleId", 1L);

        return mapper.writeValueAsString(commentRequest);
    }

    public CommentDto createCommentDto(String title, String content){
        MemberDto memberDto = MemberDto.of(1L, "some@naver.com", "123456789", LocalDateTime.now());
        CommentDto commentDto = CommentDto.of(1L, title, content, 1L, memberDto, LocalDateTime.now(), LocalDateTime.now());
        return commentDto;
    }

    @WithUserDetails
    @DisplayName("[Post]댓글 생성 테스트")
    @Test
    public void createCommentTest() throws Exception {
        String param = createRequest();

        given(commentService.createComment(any(CommentDto.class))).willReturn(createCommentDto("title", "comment"));

        mvc.perform(post("/comments")
                .content(param)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("[Post]댓글 생성 테스트(실패, 미인증)")
    @Test
    public void createCommentFailNoAuthTest() throws Exception {
        String param = createRequest();

        mvc.perform(post("/comments")
                        .content(param)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(print());

        verify(commentService, never()).createComment(any(CommentDto.class));
    }

    @WithUserDetails
    @DisplayName("[Put] 댓글 수정 테스트")
    @Test
    public void updateCommentTest() throws Exception {
        String param = createUpdateRequest();

        given(commentService.updateComment(any(CommentDto.class))).willReturn(any(CommentDto.class));

        mvc.perform(put("/comments/1")
                        .content(param)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 수정되었습니다."))
                .andDo(print());
    }

    @DisplayName("[Put] 댓글 수정 태스트(실패, 미인증)")
    @Test
    public void updateCommentFailNoAuthTest() throws Exception {
        String param = createUpdateRequest();

        mvc.perform(put("/comments/1")
                        .content(param)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(print());

        verify(commentService, never()).updateComment(any(CommentDto.class));
    }

    @WithUserDetails
    @DisplayName("[Delete] 댓글 삭제 테스트")
    @Test
    public void deleteCommentTest() throws Exception {

        willDoNothing().given(commentService).deleteComment(any(Long.class), any(Long.class));

        mvc.perform(delete("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 삭제되었습니다."))
                .andDo(print());
    }

    @DisplayName("[Delete] 댓글 삭제 테스트(실패, 미인증)")
    @Test
    public void deleteCommentNoAuthTest() throws Exception {
        mvc.perform(delete("/comments/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(print());

        verify(commentService, never()).deleteComment(any(Long.class), any(Long.class));
    }
}
