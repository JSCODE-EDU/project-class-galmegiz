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
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(article);
        try{
            em.persist(article);
            tx.commit();
        }catch(Exception e){
            tx.rollback();
        }finally{
             em.close();
        }
    }

    @Override
    public List<Article> findAll() {
        EntityTransaction tx = em.getTransaction();
        List<Article> articles = new ArrayList<>();
        try{
            articles = em.createQuery("SELECT a FROM article a", Article.class)
                    .getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        return articles;
    }

    @Override
    public Optional<Article> findById(Long id) {
        EntityTransaction tx = em.getTransaction();
        Article article = null;
        try{
            article = em.find(Article.class, id);
        }catch(Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
        return Optional.ofNullable(article);
    }

    @Override
    public void delete(Article article) {
        EntityTransaction tx = em.getTransaction();
        try{
            em.remove(article);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
}
