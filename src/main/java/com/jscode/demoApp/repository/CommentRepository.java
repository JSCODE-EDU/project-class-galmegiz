package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Comment;

import java.util.List;

public interface CommentRepository {

    public Comment save(Comment comment);
    public void delete(Comment comment);

    public List<Comment> findAllByArticleId(Long articleId);
}
