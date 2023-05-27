package com.jscode.demoApp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.config.SecurityConfig;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.MemberRegisterRequest;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.MemberDuplicateException;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import com.jscode.demoApp.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    MemberService memberService;

    private String createRegisterRequest(String email, String password) throws JsonProcessingException {
        MemberRegisterRequest memberRegisterRequest = MemberRegisterRequest.of(email, password);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(memberRegisterRequest);

    }

    @DisplayName("[POST] 회원가입 성공 테스트")
    @Test
    public void memberRegisterTest() throws Exception {
        String email = "asb@naver.com";
        String password = "123456789";
        MemberDto memberDto = MemberDto.of(1L, email, password, LocalDateTime.now());

        given(memberService.register(any(MemberDto.class))).willReturn(memberDto);


        mockMvc.perform(post("/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRegisterRequest(email, password)))
                .andExpect(status().isCreated())
                .andExpect(content().string("회원가입에 성공하였습니다."));
    }

    @DisplayName("[POST] 회원가입 실패 테스트(중복 사용자)")
    @Test
    public void memberRegisterFailByDuplicatedUserTest() throws Exception{
        String email = "asb@naver.com";
        String password = "123456789";
        given(memberService.register(any(MemberDto.class))).willThrow(new MemberDuplicateException(email));

        mockMvc.perform(post("/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRegisterRequest(email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.Member_Duplicate_Error.getCode()));
    }

    @DisplayName("[POST] 회원가입 실패 테스트(회원가입 요구조건 미충족)")
    @MethodSource("memberRegisterFailByDtoValidationTest")
    @ParameterizedTest(name = "[{index}] message : {2}")
    public void memberRegisterFailByDtoValidationTest(String email, String password, String message) throws Exception{
        MemberRegisterRequest request = MemberRegisterRequest.of(email, password);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.REQUEST_FIELD_ERROR.getCode()))
                .andDo(print());
    }

    static Stream<Arguments> memberRegisterFailByDtoValidationTest(){
        return Stream.of(
                arguments(" ", "123456789", "이메일 공백 포함"),
                arguments("ssssdf.com", "123456789", "이메일 @미포함"),
                arguments("soemf@naver.com", "1234", "pw 길이 부족"),
                arguments("sdfsdf@naver.com", "123456789012345676", "pw 길이 초과"),
                arguments("sdfsdf@naver.com", "1234 56789", "pw 공백 포함")
        );
    }




}
