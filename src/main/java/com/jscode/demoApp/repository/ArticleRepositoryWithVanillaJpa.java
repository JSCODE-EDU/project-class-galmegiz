package com.jscode.demoApp.repository;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.domain.Article;
import com.jscode.demoApp.domain.QArticle;
import com.jscode.demoApp.dto.request.PageRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static com.jscode.demoApp.domain.QArticle.*;

@Repository
public class ArticleRepositoryWithVanillaJpa implements ArticleRepository{

    @PersistenceContext
    EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public ArticleRepositoryWithVanillaJpa(EntityManager em) {
        this.em = em;
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Article save(Article article) {
        em.persist(article);
        return article;
    }

    @Override
    public List<Article> findAll(PageRequest pageRequest) {
        return em.createQuery("SELECT a FROM Article a JOIN FETCH a.member order by a.createdAt desc", Article.class)
                .setFirstResult(pageRequest.page())
                .setMaxResults(pageRequest.size() * 100)
                    .getResultList();
    }

    @Override
    public Optional<Article> findById(Long id) {
        return Optional.ofNullable(em.find(Article.class, id));
    }
    @Override
    public List<Article> findByTitle(String title, PageRequest pageRequest){
        return em.createQuery("SELECT a " +
                "from Article a " +
                "where a.title = :title " +
                "order by a.createdAt desc", Article.class)
                .setParameter("title", title)
                .setFirstResult(pageRequest.page())
                .setMaxResults(pageRequest.size() * 100)
                .getResultList();
    }

    @Override
    public void delete(Article article) {
        em.remove(article);
    }

    @Override
    public Long getCount() {
        return em.createQuery("SELECT COUNT(a) " +
                "from Article a ", Long.class)
                .getSingleResult();
    }

    @Override
    public Optional<Article> getReferenceById(Long articleId) {
        return Optional.ofNullable(em.getReference(Article.class, articleId));
    }

    @Override
    public List<Article> findBySearchType(SearchType searchtype, String searchKeyword, PageRequest pageRequest) {
        return jpaQueryFactory.select(article)
                .from(article)
                .where(searchByKeyword(searchtype, searchKeyword))
                .offset(pageRequest.page())
                .limit(pageRequest.size() * 100)
                .fetch();

    }

    private BooleanExpression searchByKeyword(SearchType searchType, String searchKeyword){
        if(searchType == SearchType.TITLE){
            return article.title.like("%" + searchKeyword + "%");
        }else if(searchType == SearchType.CONTENT){
            return article.content.like("%" + searchKeyword + "%");
        }
        return null;
    }
}
