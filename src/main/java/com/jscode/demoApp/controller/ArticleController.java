package com.jscode.demoApp.controller;

import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.dto.LikeDto;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.dto.request.PageRequest;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.dto.response.ArticleListResponseDto;
import com.jscode.demoApp.dto.response.ArticleResponse;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.AuthorizeException;
import com.jscode.demoApp.error.exception.FieldBindingException;
import com.jscode.demoApp.service.ArticleService;
import com.jscode.demoApp.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final LikeService likeService;
    private final SearchValidator searchValidator;

    @InitBinder("searchRequestDto")
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(searchValidator);
    }

    //@RequestBody 사용할 경우 해당 객체로 변환할 값 자체가 존재하지 않으면 HttpMessageNotReadableException 발생
    //메소드가 GetMapping이기 때문에 @RequestBody보다는 QueryParam으로 보내는 게 더 적절함
    //pageRequest는 입력값 오류가 있을 경우 초기값을 할당하여 로직 진행
    @GetMapping("/articles")
    public ResponseEntity getArticles(@Validated SearchRequestDto searchRequestDto, BindingResult bindingResult, PageRequest pageRequest){

        //코드 간 비교를 위해 현재 코드 형태 유지, Rest API에서는 굳이 BindingResult를 매개변수로 넘기지 않고 ExceptionHandler로 처리해도 될 듯 함
        if(bindingResult.hasErrors()){
            throw new FieldBindingException(ErrorCode.REQUEST_FIELD_ERROR, bindingResult);
        }

        List<ArticleListResponseDto> articleDtos = articleService.searchArticle(searchRequestDto, pageRequest)
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
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS, "ARTICLE");
        }
        ArticleListResponseDto newArticleResponseDto = ArticleListResponseDto.fromDto(articleService.createArticle(articleRequestDto.toArticleDto(), userPrincipal.getId()));
        URI createdUrl = new URI("/articles/" + newArticleResponseDto.getId());
        return ResponseEntity.created(createdUrl).body("게시글이 생성되었습니다.");
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity updateArticle(@RequestBody ArticleRequestDto articleRequestDto, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS, "ARTICLE");
        }
        ArticleListResponseDto updatedArticleDto = ArticleListResponseDto.fromDto(articleService.updateArticle(articleRequestDto.toArticleDto(), userPrincipal.getId()));
        return ResponseEntity.ok(updatedArticleDto);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity deleteArticle(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS, "ARTICLE");
        }
        articleService.deleteArticle(id, userPrincipal.getId());
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

    @PostMapping("/articles/{id}/like")
    public ResponseEntity likeArticle(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS, "ARTICLE LIKE");
        }

        int result = likeService.like(LikeDto.of(id, userPrincipal.toDto()));
        return ResponseEntity.status(HttpStatus.OK).body("좋아요 개수가 " + result + "되었습니다.");
    }

}
