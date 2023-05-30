package com.jscode.demoApp.dto;

import com.jscode.demoApp.domain.ArticleMemberLike;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class LikeDto {

    private Long id;
    private Long articleId;
    private MemberDto memberDto;

    public static LikeDto of(Long articleId, MemberDto memberDto){
        return new LikeDto(null, articleId, memberDto);
    }


    public static LikeDto fromEntity(ArticleMemberLike like){
        return new LikeDto(like.getId(), like.getArticle().getId(), MemberDto.fromEntity(like.getMember()));
    }


}
