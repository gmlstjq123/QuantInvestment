package com.example.QuantInvestigation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostJoinReq {
    private String id;
    private String password;
    private String accountNum; // 계좌 번호
    private String appKey;
    private String appSecret;
}
