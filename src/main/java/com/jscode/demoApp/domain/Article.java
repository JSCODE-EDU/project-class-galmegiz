package com.jscode.demoApp.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.*;


@Entity
@ToString
@Getter
@NoArgsConstructor
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;
    @Column(nullable = false) private String title;
    @Lob private String content;

    @Builder
    public Article(String title, String content){
        this.title = title;
        this.content = content;
    }
}
