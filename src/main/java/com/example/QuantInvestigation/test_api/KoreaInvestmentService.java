package com.example.QuantInvestigation.test_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class KoreaInvestmentService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${korea-investment.api.base-url}")
    private String baseUrl;

    @Value("${korea-investment.api.api-key}")    // appkey -> api-key
    private String apiKey;

    @Value("${korea-investment.api.api-secret}")  // appsecret -> api-secret
    private String apiSecret;

    @Value("${korea-investment.api.tr-id}")
    private String trId;

    @Value("${korea-investment.api.access-token}")
    private String accessToken;

    public Object getDailyPrice(String symbol) {
        log.info("Requesting daily price for symbol: {}", symbol);

        try {
            return WebClient.builder()
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/overseas-price/v1/quotations/dailyprice")
                            .queryParam("AUTH", "")
                            .queryParam("EXCD", "NAS")
                            .queryParam("SYMB", symbol)
                            .queryParam("GUBN", "0")
                            .queryParam("BYMD", "")
                            .queryParam("MODP", "1")
                            .build())
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + accessToken)
                    .header("appkey", apiKey)           // 헤더 이름은 API 스펙에 맞춰 그대로 유지
                    .header("appsecret", apiSecret)     // 헤더 이름은 API 스펙에 맞춰 그대로 유지
                    .header("tr_id", trId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            log.error("Error occurred: ", e);
            throw e;
        }
    }
}