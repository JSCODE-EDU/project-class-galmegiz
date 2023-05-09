package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Article;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ArticleRepositoryWithVanillaJpa implements ArticleRepository{

    @PersistenceContext
    EntityManager em;
    @Override
    public void save(Article article) {
        em.persist(article);
    }

    @Override
    public List<Article> findAll() {
        return em.createQuery("SELECT a FROM Article a", Article.class)
                    .getResultList();
    }

    @Override
    public Optional<Article> findById(Long id) {
        return Optional.ofNullable(em.find(Article.class, id));
    }

    @Override
    public void delete(Article article) {
        em.remove(article);
    }
}
