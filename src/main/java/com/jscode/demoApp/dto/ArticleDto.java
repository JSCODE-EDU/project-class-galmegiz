package com.jscode.demoApp.dto;

import com.jscode.demoApp.domain.Article;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;

    public ArticleDto(Long id, String title, String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }


    public static ArticleDto fromEntity(Article article){
        return new ArticleDto(article.getId(), article.getTitle(), article.getTitle());
    }
}
