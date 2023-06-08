package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    public Comment save(Comment comment);
    public void delete(Comment comment);
    public Optional<Comment> findById(Long commentId);

    public List<Comment> findAllByArticleId(Long articleId);
}
