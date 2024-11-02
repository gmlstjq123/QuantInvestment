package com.example.QuantInvestigation.chart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetCurrentPriceRes {
    private Float currentPrice;
    private Float diff;
    private Float rate;
}
