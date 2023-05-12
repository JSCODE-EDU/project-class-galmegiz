package com.jscode.demoApp.dto;

import com.jscode.demoApp.domain.Article;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;

    private LocalDateTime createdAt;

    //test용 생성자
    public ArticleDto(Long id, String title, String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }

    private ArticleDto(Long id, String title, String content, LocalDateTime createdAt){
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ArticleDto of(Long id, String title, String content){
        return new ArticleDto(id, title, content);
    }


    public static ArticleDto fromEntity(Article article){
        return new ArticleDto(article.getId(), article.getTitle(), article.getContent(), article.getCreatedAt());
    }
}
