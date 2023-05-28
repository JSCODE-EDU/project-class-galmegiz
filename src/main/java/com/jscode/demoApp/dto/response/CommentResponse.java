package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.CommentDto;
import com.jscode.demoApp.dto.MemberDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private Long id;
    private String title;
    private String content;

    private String createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CommentResponse fromDto(CommentDto commentDto) {
        return new CommentResponse(commentDto.getId(),
                commentDto.getTitle(),
                commentDto.getContent(),
                commentDto.getMemberDto().getEmail(),
                commentDto.getCreatedAt(),
                commentDto.getModifiedAt());
    }
}
