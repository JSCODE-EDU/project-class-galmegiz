package com.jscode.demoApp.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity extends BaseTimeEntity{

    @CreatedBy @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;
}
