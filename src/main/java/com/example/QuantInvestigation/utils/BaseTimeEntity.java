package com.example.QuantInvestigation.utils;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDate date;

    @CreatedDate
    private LocalTime time;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.date = now.toLocalDate();
        this.time = now.toLocalTime();
    }

}
