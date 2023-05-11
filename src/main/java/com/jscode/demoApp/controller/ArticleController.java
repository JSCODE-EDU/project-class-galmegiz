package com.jscode.demoApp.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.dto.ArticleDto;
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
    public ResponseEntity<List<ArticleDto>> getArticles(@RequestParam(name="searchType", required = false)SearchType searchType,
                                                           @RequestParam(name="searchKeyword", required = false, defaultValue = "")String searchKeyword){
        List<ArticleDto> articleDtos = articleService.searchArticle(searchType, searchKeyword);
        return ResponseEntity.ok(articleDtos);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable Long id){
        ArticleDto articleDto = null;
        try{
            articleDto = articleService.getArticle(id);
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleDto);
    }

    @PostMapping("/articles/form")
    public ResponseEntity<String> createArticle(ArticleDto articleDto) throws URISyntaxException {
        ArticleDto newArticleDto = articleService.createArticle(articleDto);
        URI createdUrl = new URI("/articles/" + newArticleDto.getId());
        return ResponseEntity.created(createdUrl).body("게시글이 생성되었습니다.");
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> updateArticle(@RequestBody ArticleDto articleDto){
        ArticleDto updatedArticleDto = null;
        try{
            updatedArticleDto = articleService.updateArticle(articleDto);
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
