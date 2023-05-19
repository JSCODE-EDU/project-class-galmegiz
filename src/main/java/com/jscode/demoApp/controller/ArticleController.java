package com.jscode.demoApp.controller;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.controller.validator.SearchValidator;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import com.jscode.demoApp.dto.response.ArticleResponseDto;
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
        //Todo : bindingResult 출력 메소드 별도 구현
        if(bindingResult.hasErrors()){
            log.info("controller check : {}", searchRequestDto);
            Map<String, List<String>> errors = new HashMap<>();
            bindingResult.getFieldErrors()
                    .forEach(e -> errors
                            .computeIfAbsent(e.getField(), key -> new ArrayList<String>())
                            .add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
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
    public ResponseEntity createArticle(@Valid ArticleRequestDto articleRequestDto,
                                        BindingResult bindingResult) throws URISyntaxException {
        /*
            bindingResult의 값을 errors Map에 담아 응답으로 보내준다.
            {에러 필드명1 : {에러 정보1, 에러 정보 2}, 에러 필드명2: {에러 정보1, 에러 정보2}라는
            형식으로 json응답을 보내고 싶을 때 Map을 쓰지 앟는 더 적절한 방법이 있을까?
            컨트롤러에서 여러 번 사용될 것 같으므로 별도 코드로 빼는 게 더 좋을 듯 하다.
         */
        if(bindingResult.hasErrors()){
            Map<String, List<String>> errors = new HashMap<>();
            bindingResult.getFieldErrors()
                            .forEach(e -> errors
                                    .computeIfAbsent(e.getField(), key -> new ArrayList<String>())
                                    .add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
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
