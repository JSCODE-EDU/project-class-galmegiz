package com.jscode.demoApp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static  org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
public class MemberIETest {
    @Autowired
    MemberService memberService;

    @Autowired private MockMvc mvc;

    @Autowired private JwtTokenProvider tokenProvider;
    String defaultToken;
    public void createToken() {
        UserPrincipal userPrincipal = UserPrincipal.of(1L, "sdfsdf@naver.com", "{noop}123456789");
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(userPrincipal, userPrincipal.getPassword(), Set.of());
        String token = tokenProvider.createToken(authenticationToken);
        defaultToken =  JwtTokenProvider.HEADER_PREFIX + " " + token;
    }

    @Test
    @DisplayName("[POST][저장] 회원가입 테스트")
    public void memberRegisterTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("email", "som@naver.com");
        params.put("password", "123456789");



        mvc.perform(post("/members")
                .content(mapper.writeValueAsString(params))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("회원가입에 성공하였습니다."));

    }

    @Test
    @DisplayName("[POST][저장] 회원가입 테스트(실패, 사용자 중복)")
    public void memberRegisterFailDuplicateMemberTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("email", "sdfsdf@naver.com");
        params.put("password", "123456789");



        mvc.perform(post("/members")
                        .content(mapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.Member_Duplicate_Error.getCode()));

    }



    @Test
    @DisplayName("[GET][조회] 회원 정보 조회 테스트")
    public void getMemberInfoTest() throws Exception {
        Long userId = 1L;
        createToken();

        mvc.perform(get("/members/{id}", userId)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sdfsdf@naver.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("[GET][조회] 회원 정보 조회 테스트(실패, 사용자 미인증)")
    public void getMemberInfoFailNoAuthTest() throws Exception {
        Long userId = 1L;


        mvc.perform(get("/members/{id}", userId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.JWT_TOKEN_ERROR.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[GET][조회] 회원 정보 조회 테스트(실패, Id 미일치)")
    public void getMemberInfoFailNotMatchedIdTest() throws Exception {
        Long userId = 2L;
        createToken();

        mvc.perform(get("/members/{id}", userId)
                        .header(JwtTokenProvider.AUTHORIZATION_HEADER, defaultToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS.getCode()))
                .andDo(print());
    }
}
