package com.example.QuantInvestigation.error_log;

import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorLog extends BaseTimeEntity {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId; // 고유 식별자

    @Column(nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ErrorLog createHistory(String message, User user) {
        this.message = message;
        this.user= user;
        return this;
    }

}

