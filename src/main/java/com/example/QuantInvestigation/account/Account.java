package com.example.QuantInvestigation.account;

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
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false)
    private String accountNumber; // 계좌번호

    @Column(nullable = false)
    private Float assessmentFee; // 평가금

    @Column(nullable = false)
    private Float purchasePrice; // 매입금

    @Column(nullable = false)
    private Float deposit; // 예수금

    public Account createAccount(String accountNumber, Float assessmentFee, Float purchasePrice, Float deposit) {
        this.accountNumber = accountNumber;
        this.assessmentFee = assessmentFee;
        this.purchasePrice= purchasePrice;
        this.deposit = deposit;
        return this;
    }
}
