package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Comment;
import com.jscode.demoApp.dto.CommentDto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
@Repository
public class CommentRepositoryWithVanillaJpa implements CommentRepository{

    @PersistenceContext
    EntityManager em;

    @Override
    public Comment save(Comment comment) {
        em.persist(comment);
        return comment;
    }

    @Override
    public void delete(Comment comment) {
        em.remove(comment);
    }
}
