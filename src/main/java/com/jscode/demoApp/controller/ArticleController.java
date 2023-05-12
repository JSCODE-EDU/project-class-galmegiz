package com.jscode.demoApp.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
import com.jscode.demoApp.dto.request.ArticleRequestDto;
import com.jscode.demoApp.dto.response.ArticleResponseDto;
import com.jscode.demoApp.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponseDto>> getArticles(@RequestParam(name="searchType", required = false)SearchType searchType,
                                                                @RequestParam(name="searchKeyword", required = false, defaultValue = "")String searchKeyword){
        List<ArticleResponseDto> articleDtos = articleService.searchArticle(searchType, searchKeyword)
                                                                .stream()
                                                                .map(ArticleResponseDto::fromDto)
                                                                .toList();
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleResponseDto> getArticle(@PathVariable Long id){
        ArticleResponseDto articleResponseDto = null;
        try{
            articleResponseDto = ArticleResponseDto.fromDto(articleService.getArticle(id));
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleResponseDto);
    }

    @PostMapping("/articles/form")
    public ResponseEntity<String> createArticle(ArticleRequestDto articleRequestDto) throws URISyntaxException {
        ArticleDto newArticleDto = articleService.createArticle(articleRequestDto.toArticleDto());
        URI createdUrl = new URI("/articles/" + newArticleDto.getId());
        return ResponseEntity.created(createdUrl).body("게시글이 생성되었습니다.");
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<ArticleResponseDto> updateArticle(@RequestBody ArticleRequestDto articleRequestDto){
        ArticleResponseDto updatedArticleDto = null;
        try{
            updatedArticleDto = ArticleResponseDto.fromDto(articleService.updateArticle(articleRequestDto.toArticleDto()));
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedArticleDto);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id){
        try{
            articleService.deleteArticle(id);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

}
