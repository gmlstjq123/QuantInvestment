package com.example.QuantInvestigation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetTotalBalanceRes {
    private Float totalLoss; // 총 손실
    private Float realizedLoss; // 실현 손실
    private Float realizedRate; // 실현 수익율
    private Float totalRatingLoss; // 전체 평가 손실
    private Float totalRate; // 전체 수익율
}
