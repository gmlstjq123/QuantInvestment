package com.example.QuantInvestigation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetItemBalanceRes {
    private String itemCode;
    private Float ratingLossAmount; // 평가 손실 금액
    private Float valuationLossRate; // 평가 손실율
    private Float averagePurchasePrice; // 매입 평균 가격
    private Integer quantity; // 보유 수량
    private Float valuationAmount; // 평가 금액
}
