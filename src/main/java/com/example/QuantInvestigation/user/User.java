package com.example.QuantInvestigation.user;

import com.example.QuantInvestigation.buy_shares.BuyShares;
import com.example.QuantInvestigation.error_log.ErrorLog;
import com.example.QuantInvestigation.user.user_option.UserOption;
import com.example.QuantInvestigation.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity {
    @Column
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 고유 식별자

    @Column(nullable = false)
    private String id; // 로그인 ID

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String appKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String appSecret;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken; // 증권사 Token

    @Column(nullable = false)
    private String accountNumber; // 계좌번호

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ErrorLog> errorLogList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserOption userOption; // 매매 옵션과 일대일 매핑

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuyShares> boards = new ArrayList<>(); // 매수 주식과 일대다 매핑

    public User createUser(String id, String password, String appKey, String appSecret, String accessToken, String accountNumber) {
        this.id = id;
        this.password = password;
        this.appKey= appKey;
        this.appSecret = appSecret;
        this.accessToken = accessToken;
        this.accountNumber = accountNumber;
        return this;
    }

}
