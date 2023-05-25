package com.jscode.demoApp.dto;


import com.jscode.demoApp.domain.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberDto {
    private Long id;
    private String email;
    private String password;

    private MemberDto(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
    //todo : 테스트 코드 때문에 id 필드를 만들어줬다. 없앨 순 없을까?
    public static MemberDto of(Long id, String email, String password){
        return new MemberDto(id, email, password);
    }
    public static MemberDto of(String email, String password){
        return MemberDto.of(null, email, password);
    }

    public static MemberDto fromEntity(Member member){
        return new MemberDto(member.getId(), member.getEmail(), member.getPassword());
    }

}
