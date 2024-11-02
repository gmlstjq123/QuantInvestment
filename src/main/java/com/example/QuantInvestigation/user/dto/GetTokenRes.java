package com.example.QuantInvestigation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetTokenRes {
    private String access_token;
    private String access_token_token_expired;
    private String token_type;
    private Integer expires_in;
}