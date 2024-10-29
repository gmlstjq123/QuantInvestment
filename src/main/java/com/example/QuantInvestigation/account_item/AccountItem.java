package com.example.QuantInvestigation.account_item;

import com.example.QuantInvestigation.account.Account;
import com.example.QuantInvestigation.item.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userChatRoomId;

    @Column(nullable = false)
    private Integer holdings; // 보유 수량

    @Column(nullable = false)
    private Float profitLossValuation; // 평가 손익

    @Column(nullable = false)
    private Float returnRate; // 수익율

    @Column(nullable = false)
    private Float purchasePrice; // 매입 단가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void setAccount(Account account){
        this.account = account;
    }

    public void setUser(Item item){
        this.item = item;
    }
}
