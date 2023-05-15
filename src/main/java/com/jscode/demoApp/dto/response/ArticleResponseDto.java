package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class ArticleResponseDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;
    private LocalDateTime createdAt;


    private ArticleResponseDto(Long id, String title, String content, LocalDateTime createdAt){
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ArticleResponseDto fromDto(ArticleDto articleDto){
        return new ArticleResponseDto(articleDto.getId(),
                                        articleDto.getTitle(),
                                        articleDto.getContent(),
                                        articleDto.getCreatedAt());
    }
}
