package com.example.QuantInvestigation.test_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/stocks")
@Slf4j
public class KoreaInvestmentController {

    @Autowired
    private KoreaInvestmentService koreaInvestmentService;

    @GetMapping("/daily-price/{symbol}")
    public ResponseEntity<?> getDailyPrice(@PathVariable("symbol") String symbol) {  // name 속성 추가
        try {
            Object result = koreaInvestmentService.getDailyPrice(symbol);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error: ", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
