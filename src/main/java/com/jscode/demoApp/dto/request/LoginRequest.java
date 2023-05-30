package com.jscode.demoApp.dto.request;

import com.jscode.demoApp.dto.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class LoginRequest {


    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9.-]+$", message = "올바른 이메일형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "^(?=.{8,15}$)[^\s]*$", message = "비밀번호는 8자 이상 15자 이하입니다.")
    private String password;

    private LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginRequest of(String email, String password){
        return new LoginRequest(email, password);
    }

    public MemberDto toMemberDto() {
        return MemberDto.of(this.email, this.password);
    }
}
