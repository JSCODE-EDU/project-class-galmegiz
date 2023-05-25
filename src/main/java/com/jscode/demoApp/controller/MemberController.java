package com.jscode.demoApp.controller;

import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.MemberRegisterRequest;
import com.jscode.demoApp.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity memberRegister(@RequestBody @Validated MemberRegisterRequest memberRegisterRequest) throws URISyntaxException {
        MemberDto memberDto = memberService.register(memberRegisterRequest.toDto());
        System.out.println(memberDto);
        Long createdId = memberDto.getId();
        URI createdUrl = new URI("/members/" + createdId);
        return ResponseEntity.created(createdUrl).body("회원가입을 성공하였습니다.");
    }
}
