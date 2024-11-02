package com.example.QuantInvestigation.chart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetPeriodPriceRes {
    private String baseDate; // 오늘
    private Float open; // 시가
    private Float high; // 고가
    private Float low; // 저가
    private Float close; // 종가
    private Float rate; // 등락률
}
