package com.jscode.demoApp.domain;

import lombok.*;

import javax.persistence.*;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Builder
    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }
}