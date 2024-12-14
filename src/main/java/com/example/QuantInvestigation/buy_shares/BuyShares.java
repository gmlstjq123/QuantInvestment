package com.example.QuantInvestigation.buy_shares;

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
public class BuyShares extends BaseTimeEntity {

    @Column
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buySharesId; // 고유 식별자

    @Column(nullable = false)
    private String ticker; // 티커

    @Column(nullable = false)
    private Integer qty; // 매수 수량

    @Column(nullable = false)
    private Float price; // 매입 단가

    @Column(nullable = false)
    private Integer retentionPeriod; // 보유 가능 기간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 유저와 관계 매핑

    public BuyShares createBuyShares(Integer qty, Float price, Integer retentionPeriod) {
        this.qty = qty;
        this.price = price;
        this.retentionPeriod= retentionPeriod;
        return this;
    }

}