package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.dto.ArticleDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ArticleListResponseDto {
    private Long id;

    private String title;

    private String content;
    private LocalDateTime createdAt;
    private String createdBy;





    public static ArticleListResponseDto fromDto(ArticleDto articleDto){
        return new ArticleListResponseDto(articleDto.getId(),
                                        articleDto.getTitle(),
                                        articleDto.getContent(),
                                        articleDto.getCreatedAt(),
                                        articleDto.getMember().getEmail());
    }
}
