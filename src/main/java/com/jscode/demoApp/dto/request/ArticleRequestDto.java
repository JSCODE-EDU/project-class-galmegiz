package com.jscode.demoApp.dto.request;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class ArticleRequestDto {
    private Long id;
    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(min = 1, max = 15, message = "제목은 15자를 넘을 수 없습니다.")
    @Setter
    private String title;
    @NotNull(message = "내용은 필수 입력값입니다.")
    @Size(min = 1, max = 1000, message = "내용은 최대 1000자를 넘을 수 없습니다.")
    @Setter
    private String content;
    private LocalDateTime createdAt;

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
