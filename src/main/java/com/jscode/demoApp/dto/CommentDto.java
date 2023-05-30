package com.jscode.demoApp.dto;

import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDto {
    private Long id;
    private String title;
    private String content;
    private Long articleId;
    private MemberDto memberDto;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static CommentDto of(Long id, String title, String content, Long articleId, MemberDto memberDto, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new CommentDto(id, title, content, articleId, memberDto, createdAt, modifiedAt);
    }
    public static CommentDto of(String title, String content, Long articleId, MemberDto memberDto) {
        return new CommentDto(null, title, content, articleId, memberDto, null, null);
    }

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.of(comment.getId(),
                comment.getTitle(),
                comment.getComment(),
                comment.getArticle().getId(),
                MemberDto.fromEntity(comment.getMember()),
                comment.getCreatedAt(),
                comment.getLastModifiedAt());
    }
}
