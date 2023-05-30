package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
    public List<Comment> findAllByArticleId(Long articleId) {
        return em.createQuery("SELECT c " +
                "FROM Comment c " +
                "WHERE c.id = :id " +
                "ORDER BY c.createdAt", Comment.class)
                .setParameter("id", articleId)
                .getResultList();
    }

    @Override
    public void delete(Comment comment) {
        em.remove(comment);
    }
}
