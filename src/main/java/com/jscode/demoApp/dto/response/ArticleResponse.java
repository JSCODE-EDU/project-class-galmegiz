package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.dto.ArticleDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private String createdBy;
    private LocalDateTime createdAt;
    private int likeCount;
    private List<CommentResponse> commentResponses;





    public static ArticleResponse fromDto(ArticleDto articleDto){
        return new ArticleResponse(articleDto.getId(),
                articleDto.getTitle(),
                articleDto.getContent(),
                articleDto.getMember().getEmail(),
                articleDto.getCreatedAt(),
                articleDto.getLikeDtos().size(),
                articleDto.getCommentDtos().stream().map(CommentResponse::fromDto).toList()
                );
    }
}
