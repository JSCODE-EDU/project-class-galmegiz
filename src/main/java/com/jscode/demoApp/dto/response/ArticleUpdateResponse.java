package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.dto.ArticleDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ArticleUpdateResponse {
    private Long id;

    private String title;

    private String content;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

    public static ArticleUpdateResponse fromDto(ArticleDto articleDto){
        return new ArticleUpdateResponse(articleDto.getId(),
                articleDto.getTitle(),
                articleDto.getContent(),
                articleDto.getModifiedAt(),
                articleDto.getMember().getEmail());
    }
}
