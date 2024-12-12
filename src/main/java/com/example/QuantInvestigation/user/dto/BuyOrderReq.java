package com.example.QuantInvestigation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuyOrderReq {
    private String ticker;
    private String purchasePrice;
    private Integer qty;
}
