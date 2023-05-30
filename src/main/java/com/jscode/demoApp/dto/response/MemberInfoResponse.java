package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.dto.MemberDto;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberInfoResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;

    public static MemberInfoResponse of(Long id, String email, LocalDateTime createdAt){
        return new MemberInfoResponse(id, email, createdAt);
    }

    public static MemberInfoResponse fromDto(MemberDto memberDto){
        return MemberInfoResponse.of(memberDto.getId(), memberDto.getEmail(), memberDto.getCreatedAt());
    }
}
