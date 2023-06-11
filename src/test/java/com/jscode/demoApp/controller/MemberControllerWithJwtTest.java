package com.jscode.demoApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.config.SecurityConfig;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.LoginRequest;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.LoginFailException;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.service.MemberService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@Import({SecurityConfig.class, JwtTokenProvider.class})
public class MemberControllerWithJwtTest {

    @Autowired
    MockMvc mvc;

    @MockBean MemberService memberService;

    /*
    테스트 오류 원인 : 처음에는 MemberDto.of(id, "123456789", localdateTime.now()) 했더니 password 검증 과정에서 There is no PasswordEncoder mapped for the id "null" 익셉션 발생
     */
    @Test
    @DisplayName("[POST] 로그인 성공 테스트")
    public void loginSuccessTest() throws Exception {
        LoginRequest request = LoginRequest.of("aldflf@naver.com", "1234556789");
        given(memberService.findMemberByEmail(request.getEmail())).willReturn(MemberDto.of(1L, "aldflf@naver.com", "{noop}1234556789", LocalDateTime.now()));
        ObjectMapper mapper = new ObjectMapper();

        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                    )
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공"))
                .andExpect(header().exists("Authorization"));
    }


    @Test
    @DisplayName("[POST] 로그인 실패 테스트")
    public void loginFailTest() throws Exception {
        LoginRequest request = LoginRequest.of("aldflf@naver.com", "1234556789");
        given(memberService.findMemberByEmail(request.getEmail())).willThrow(new LoginFailException());
        ObjectMapper mapper = new ObjectMapper();

        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.Login_Fail.getCode()))
                .andDo(print());
    }

    @Disabled
    @Test
    @DisplayName("[GET] 회원정보 조회 성공 테스트")
    public void getMemberInfoTest(){

    }

    @Test
    @DisplayName("[GET] 회원정보 조회 실패 테스트(미인증)")
    public void getMemberInfoFailByUnauthorizedTest() throws Exception {
        //service 코드 실행되지 않으므로 mocking 불필요

        mvc.perform(get("/members/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.JWT_TOKEN_ERROR.getCode()))
                .andDo(print());
    }


}
