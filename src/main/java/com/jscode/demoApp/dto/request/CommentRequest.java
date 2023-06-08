package com.jscode.demoApp.dto.request;

import com.jscode.demoApp.dto.CommentDto;
import com.jscode.demoApp.dto.MemberDto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class CommentRequest {
    private Long id;
    private String title;
    private String content;
    private Long articleId;

    public CommentDto toDto(MemberDto memberDto){
        return CommentDto.of(this.id, this.title, this.content, articleId, memberDto, null, null);
    }
}
