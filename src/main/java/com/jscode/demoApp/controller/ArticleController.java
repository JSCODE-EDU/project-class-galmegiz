package com.jscode.demoApp.controller;

import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.dto.response.ArticleListResponseDto;
import com.jscode.demoApp.dto.response.ArticleResponse;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.AuthorizeException;
import com.jscode.demoApp.error.exception.FieldBindingException;
import com.jscode.demoApp.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final SearchValidator searchValidator;

    @InitBinder("searchRequestDto")
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(searchValidator);
    }

    @GetMapping("/articles")
    public ResponseEntity getArticles(@Validated SearchRequestDto searchRequestDto, BindingResult bindingResult){

        //코드 간 비교를 위해 현재 코드 형태 유지, Rest API에서는 굳이 BindingResult를 매개변수로 넘기지 않고 ExceptionHandler로 처리해도 될 듯 함
        if(bindingResult.hasErrors()){
            throw new FieldBindingException(ErrorCode.REQUEST_FIELD_ERROR, bindingResult);
        }
        List<ArticleListResponseDto> articleDtos = articleService.searchArticle(searchRequestDto)
                                                                .stream()
                                                                .map(ArticleListResponseDto::fromDto)
                                                                .toList();
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity getArticle(@PathVariable Long id){
        ArticleResponse articleResponse = ArticleResponse.fromDto(articleService.getArticle(id));
        return ResponseEntity.ok(articleResponse);
    }
/*
    @PostMapping("/articles/form")
    public ResponseEntity createArticle(@Valid ArticleRequestDto articleRequestDto) throws URISyntaxException {
        ArticleResponseDto newArticleResponseDto = ArticleResponseDto.fromDto(articleService.createArticle(articleRequestDto.toArticleDto()));
        URI createdUrl = new URI("/articles/" + newArticleResponseDto.getId());
        return ResponseEntity.created(createdUrl).body("게시글이 생성되었습니다.");
    }

 */

    //@PreAuthorize("isAuthenticated()") -> 익셉션 처리 추가 공부 필요
    @PostMapping("/articles/form")
    public ResponseEntity createArticle(@Valid ArticleRequestDto articleRequestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) throws URISyntaxException {
        if(userPrincipal == null){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
        }
        ArticleListResponseDto newArticleResponseDto = ArticleListResponseDto.fromDto(articleService.createArticle(articleRequestDto.toArticleDto(), userPrincipal.getId()));
        URI createdUrl = new URI("/articles/" + newArticleResponseDto.getId());
        return ResponseEntity.created(createdUrl).body("게시글이 생성되었습니다.");
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity updateArticle(@RequestBody ArticleRequestDto articleRequestDto, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
        }
        ArticleListResponseDto updatedArticleDto = ArticleListResponseDto.fromDto(articleService.updateArticle(articleRequestDto.toArticleDto(), userPrincipal.getId()));
        return ResponseEntity.ok(updatedArticleDto);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity deleteArticle(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
        }
        articleService.deleteArticle(id, userPrincipal.getId());
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }



}
