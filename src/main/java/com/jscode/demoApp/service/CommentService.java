package com.jscode.demoApp.service;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.CommentDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.ResourceCreationException;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.repository.ArticleRepository;
import com.jscode.demoApp.repository.CommentRepository;
import com.jscode.demoApp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    public CommentDto createComment(CommentDto commentDto){
        Comment comment = null;
        Optional<Article> article = articleRepository.findById(commentDto.getArticleId());
        Optional<Member> member = memberRepository.findById(commentDto.getMemberId());

        //Spring JPA 쓰면 getRerefenceById를 이용해 repository 단에서 throw시킬 수도 있음
        if(article.isEmpty() || member.isEmpty()){
            throw new ResourceCreationException(ErrorCode.COMMENT_CREATE_ERROR, article.isEmpty() ? "article" : "member");
        }


        comment = Comment.builder().title(commentDto.getTitle())
                .comment(commentDto.getContent())
                .article(article.get())
                .member(member.get())
                .build();

        commentRepository.save(comment);

        return CommentDto.fromEntity(comment);
    }
}
