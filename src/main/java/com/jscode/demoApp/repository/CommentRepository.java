package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.dto.CommentDto;

public interface CommentRepository {

    public Comment save(Comment comment);
    public void delete(Comment comment);
}
