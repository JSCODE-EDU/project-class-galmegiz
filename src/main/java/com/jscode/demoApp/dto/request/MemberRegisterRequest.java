package com.jscode.demoApp.dto.request;

import com.jscode.demoApp.dto.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Pattern;

@Getter
@ToString
@NoArgsConstructor
public class MemberRegisterRequest {


    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9.-]+$", message = "올바른 이메일형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "^(?=.{8,15}$)[^\s]*$", message = "비밀번호는 8자 이상 15자 이하입니다.")
    private String password;

    private MemberRegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static MemberRegisterRequest of(String email, String password){
        return new MemberRegisterRequest(email, password);
    }

    public MemberDto toDto(){
        return MemberDto.of(this.email, this.password);
    }
}
