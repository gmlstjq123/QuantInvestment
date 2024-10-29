package com.example.QuantInvestigation.user;

import com.example.QuantInvestigation.account.Account;
import com.example.QuantInvestigation.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity {
    @Column
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String appKey;

    @Column(nullable = false)
    private String appSecret;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public User createUser(String appKey, String appSecret) {
        this.appKey= appKey;
        this.appSecret = appSecret;
        return this;
    }
}
