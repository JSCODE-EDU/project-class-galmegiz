package com.jscode.demoApp.controller;

import com.jscode.demoApp.dto.CommentDto;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.dto.request.CommentRequest;
import com.jscode.demoApp.dto.response.CommentResponse;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.AuthorizeException;
import com.jscode.demoApp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class CommentController {
   private final CommentService commentService;

   @PostMapping("/comments")
    public ResponseEntity createComment(@RequestBody CommentRequest commentRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) throws URISyntaxException {
       if(userPrincipal == null){
           throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
       }
       CommentDto createdComment = commentService.createComment(commentRequest.toDto(userPrincipal.toDto()));
       URI createdUrl = new URI("/comments/" + createdComment.getId());
       return ResponseEntity.created(createdUrl).body(CommentResponse.fromDto(createdComment));
   }

   @PutMapping("/comments/{id}")
    public ResponseEntity updateComment(@RequestBody CommentRequest commentRequest,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal){
       if(userPrincipal == null){
           throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
       }

       commentService.updateComment(commentRequest.toDto(userPrincipal.toDto()));
       return ResponseEntity.ok("댓글이 수정되었습니다.");
   }

   @DeleteMapping("/comments/{id}")
    public ResponseEntity deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){
       if(userPrincipal == null){
           throw new AuthorizeException(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
       }
       commentService.deleteComment(id, userPrincipal.getId());
       return ResponseEntity.ok("댓글이 삭제되었습니다.");
   }
}
