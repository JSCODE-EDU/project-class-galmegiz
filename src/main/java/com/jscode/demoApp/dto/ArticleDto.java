package com.jscode.demoApp.dto;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime ModifiedAt;

    private Member member;
    private List<CommentDto> commentDtos;

    private List<LikeDto> likeDtos;



    public static ArticleDto of(Long id, String title, String content){
        return new ArticleDto(id, title, content, null, null, null, null, null);
    }




    public static ArticleDto fromEntity(Article article){
        return new ArticleDto(article.getId()
                ,article.getTitle()
                ,article.getContent()
                ,article.getCreatedAt()
                ,article.getLastModifiedAt()
                ,article.getMember()
                ,article.getComments().stream().map(CommentDto::fromEntity).toList()
                ,article.getLikes().stream().map(LikeDto::fromEntity).toList()
        );
    }

    public static ArticleDto fromEntities(Article article){
        return new ArticleDto(article.getId()
                ,article.getTitle()
                ,article.getContent()
                ,article.getCreatedAt()
                ,article.getLastModifiedAt()
                ,article.getMember()
                ,null
                ,null
        );
    }

    public Article toEntity(){
        return Article.builder().title(this.title)
                .content(this.content)
                .build();
    }
}
