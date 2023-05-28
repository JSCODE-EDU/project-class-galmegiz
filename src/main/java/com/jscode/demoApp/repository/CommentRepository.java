package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.dto.CommentDto;

import java.util.List;

public interface CommentRepository {

    public Comment save(Comment comment);
    public void delete(Comment comment);

    public List<Comment> findAllByArticleId(Long articleId);
}
