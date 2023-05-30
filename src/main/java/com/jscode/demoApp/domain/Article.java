package com.jscode.demoApp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@ToString
@Getter
@NoArgsConstructor
public class Article extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;
    @Column(nullable = false) private String title;
    @Lob private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    Member member;


    @ToString.Exclude
    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<ArticleMemberLike> likes = new ArrayList<>();

    @Builder
    public Article(String title, String content, Member member){
        this.title = title;
        this.content = content;
        this.member = member;
    }
    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

}
