package com.jscode.demoApp.controller;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.dto.response.ArticleResponseDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.FieldBindingException;
import com.jscode.demoApp.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<ArticleResponseDto> articleDtos = articleService.searchArticle(searchRequestDto)
                                                                .stream()
                                                                .map(ArticleResponseDto::fromDto)
                                                                .toList();
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity getArticle(@PathVariable Long id){
        ArticleResponseDto articleResponseDto = ArticleResponseDto.fromDto(articleService.getArticle(id));
        return ResponseEntity.ok(articleResponseDto);
    }

    @PostMapping("/articles/form")
    public ResponseEntity createArticle(@Valid ArticleRequestDto articleRequestDto) throws URISyntaxException {
        ArticleResponseDto newArticleResponseDto = ArticleResponseDto.fromDto(articleService.createArticle(articleRequestDto.toArticleDto()));
        URI createdUrl = new URI("/articles/" + newArticleResponseDto.getId());
        return ResponseEntity.created(createdUrl).body("게시글이 생성되었습니다.");
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity updateArticle(@RequestBody ArticleRequestDto articleRequestDto){
        ArticleResponseDto updatedArticleDto = ArticleResponseDto.fromDto(articleService.updateArticle(articleRequestDto.toArticleDto()));
        return ResponseEntity.ok(updatedArticleDto);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity deleteArticle(@PathVariable Long id){
        articleService.deleteArticle(id);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

}
