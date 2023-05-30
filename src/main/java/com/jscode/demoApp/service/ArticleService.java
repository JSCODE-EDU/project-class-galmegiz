package com.jscode.demoApp.service;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Member;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.dto.request.PageRequest;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.AuthorizeException;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import com.jscode.demoApp.repository.ArticleRepository;
import com.jscode.demoApp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional //레포지토리에 태그다는 건 안 되는가?
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;

    public ArticleDto getArticle(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
            throw new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND);
            //아래 코드로도 예외처리 가능
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.", new EntityNotFoundException());
        });


        return ArticleDto.fromEntity(article);
    }

    public List<ArticleDto> searchArticle(SearchRequestDto searchRequestDto, PageRequest pageRequest){
        List<Article> articles = new ArrayList<>();

        if(searchRequestDto.getSearchType() == null){
            articles = articleRepository.findAll(pageRequest);
        }else if(searchRequestDto.getSearchType() == SearchType.TITLE){
            articles = articleRepository.findByTitle(searchRequestDto.getSearchKeyword(), pageRequest);
        }else{ //ToDo : 향후 구현
            articles = articleRepository.findAll(pageRequest);
        }
        System.out.println("=============after sql=============");
        //현재 상태에서는 select article 이후 dto 변환 과정에서 member와 like에 대한 추가 select 쿼리를 각 2번 실행한다.
        //쿼리 최적화를 하려면 fetch join을 하든지 dto를 수정해줘야할 듯 하다.
        return articles.stream().map(ArticleDto::fromEntities).toList();
    }
    //getArticles를 빼는 게 나을까?
    public List<ArticleDto> getAllArticles(PageRequest pageRequest){
        List<Article> articles = articleRepository.findAll(pageRequest);
        return articles.stream().map(ArticleDto::fromEntities).toList();
    }

    public ArticleDto createArticle(ArticleDto articleDto, Long userId){ //메소드명은 save로 통일하는 게 좋은가?
        Member member = memberService.findMemberById(userId).toEntity();

        Article newArticle = Article.builder()
                                    .title(articleDto.getTitle())
                                    .content(articleDto.getContent())
                                    .member(member)
                                    .build();

        articleRepository.save(newArticle);

        return ArticleDto.fromEntity(newArticle);
    }

    public void deleteArticle(Long articleId, Long userId){
        Article article = requestAuthorizeCheck(articleId, userId);
        articleRepository.delete(article);
    }

    public ArticleDto updateArticle(ArticleDto articleDto, Long userId){
        Article article = requestAuthorizeCheck(articleDto.getId(), userId);
        article.update(articleDto.getTitle(), articleDto.getContent());
        return ArticleDto.fromEntity(article);
    }

    private Article requestAuthorizeCheck(Long articleId, Long userId){
        MemberDto memberDto = memberService.findMemberById(userId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(ErrorCode.ARTICLE_NOT_FOUND);
                });

        if(article.getMember().getId() != memberDto.getId()){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
        }

        return article;
    }



}
