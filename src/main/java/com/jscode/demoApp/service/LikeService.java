package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.ArticleMemberLike;
import com.jscode.demoApp.dto.LikeDto;
import com.jscode.demoApp.repository.ArticleRepositoryWithSpring;
import com.jscode.demoApp.repository.LikeRepository;
import com.jscode.demoApp.repository.MemberRepositoryWithSpring;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final ArticleRepositoryWithSpring articleRepository;
    private final MemberRepositoryWithSpring memberRepository;

    public int like(LikeDto likeDto){
        /*
        Hibernate:
            select
                like_id,
                article_id,
                member_id
            from
                article_member_like as aml
            inner join
                article
                    on aml.article_id=article.article_id
            inner join
                member
                    on aml.member_id=member_.member_id
            where
                article.article_id=likeDto.getArticleId()
                and member.member_id=likeDto.getMemberId()
         */


        Optional<ArticleMemberLike> like = likeRepository.findByArticle_IdAndMember_Id(likeDto.getArticleId(), likeDto.getMemberDto().getId());
        if(like.isPresent()){
            dislike(like.get());
            return -1;
        }

        // 위 구문에서 찾아진 id가 있다면  getReferenceById의 경우 추가 쿼리를 실행하지 않음
        /* 반면에 그냥 findById를 쓰면 아래 쿼리 실행
        Hibernate:
                select
                    article.article_id as article_1_0_0_,
                    article.created_at as created_2_0_0_,
                    article.last_modified_at as last_mod3_0_0_,
                    article.created_by as created_4_0_0_,
                    article.modified_by as modified5_0_0_,
                    article_.content as content6_0_0_,
                    article_.member_id as member_i8_0_0_,
                    article_.title as title7_0_0_,
                    member_.member_id as member_i1_3_1_,
                    member_.created_at as created_2_3_1_,
                    member_.last_modified_at as last_mod3_3_1_,
                    member_.email as email4_3_1_,
                    member_.password as password5_3_1_
                from
                    article
                inner join
                    member
                        on article.member_id=member_.member_id
                where
                    article.article_id=?

         */
        ArticleMemberLike newLike = ArticleMemberLike.of(
                articleRepository.getReferenceById(likeDto.getArticleId()),
                memberRepository.getReferenceById(likeDto.getMemberDto().getId())
        );


        likeRepository.save(newLike);
        return 1;

    }

    public int dislike(ArticleMemberLike like){
        likeRepository.delete(like);
        return -1;
    }
}
