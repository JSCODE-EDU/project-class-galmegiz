package com.jscode.demoApp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
    @Autowired MockMvc mvc;

    @Test
    @DisplayName("게시물 전체 조회 컨트롤러 테스트")
    public void getAllArticlesTest() throws Exception {
        mvc.perform(get("/articles"))
                .andExpect(status().isOk());
    }
}
