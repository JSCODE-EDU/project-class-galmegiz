package com.jscode.demoApp.controller;

import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.dto.request.LoginRequest;
import com.jscode.demoApp.dto.request.MemberRegisterRequest;
import com.jscode.demoApp.dto.response.MemberInfoResponse;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.AuthorizeException;
import com.jscode.demoApp.service.MemberService;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor
@RestController
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity memberRegister(@RequestBody @Validated MemberRegisterRequest memberRegisterRequest) throws URISyntaxException {
        MemberDto memberDto = memberService.register(memberRegisterRequest.toDto());
        Long createdId = memberDto.getId();
        URI createdUrl = new URI("/members/" + createdId);
        return ResponseEntity.created(createdUrl).body("회원가입에 성공하였습니다.");
    }

    /* Spring Security 이용 로그인 사용하므로 컨트롤러 로그인은 불필요
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginRequest loginRequest){
        String token = memberService.login(loginRequest.toMemberDto());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtTokenProvider.AUTHORIZATION_HEADER, "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(token);
    }
    */

    @GetMapping("/members/{id}")
    public ResponseEntity getMemberInfo(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            log.info("{}", "error");
            throw new AuthorizeException(ErrorCode.JWT_TOKEN_ERROR);
        }

        if(!userPrincipal.getId().equals(id)){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS, "MEMBER_INFO");
        }

        MemberInfoResponse response = MemberInfoResponse.fromDto(memberService.findMemberById(userPrincipal.getId()));
        return ResponseEntity.ok(response);
    }


}
