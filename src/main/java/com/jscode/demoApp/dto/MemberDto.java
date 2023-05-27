package com.jscode.demoApp.dto;


import com.jscode.demoApp.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private LocalDateTime createdAt;


    //todo : 테스트 코드 때문에 id 필드를 만들어줬다. 없앨 순 없을까?
    public static MemberDto of(Long id, String email, String password, LocalDateTime createdAt){
        return new MemberDto(id, email, password, createdAt);
    }
    public static MemberDto of(String email, String password){
        return MemberDto.of(null, email, password, null);
    }

    public static MemberDto fromEntity(Member member){
        return new MemberDto(member.getId(), member.getEmail(), member.getPassword(), member.getCreatedAt());
    }

}
