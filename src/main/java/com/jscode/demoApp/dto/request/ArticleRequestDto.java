package com.jscode.demoApp.dto.request;

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
public class ArticleRequestDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;

    private LocalDateTime createdAt;

    //test용 생성자


    private ArticleRequestDto(Long id, String title, String content, LocalDateTime createdAt){
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }


    public ArticleDto toArticleDto(){
        return ArticleDto.of(this.getId(), this.getTitle(), this.getContent());
    }
}
