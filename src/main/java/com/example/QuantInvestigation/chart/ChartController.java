package com.example.QuantInvestigation.chart;

import com.example.QuantInvestigation.chart.dto.GetCurrentPriceRes;
import com.example.QuantInvestigation.chart.dto.GetPeriodPriceRes;
import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.exception.BaseResponse;
import com.example.QuantInvestigation.token.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chart")
public class ChartController {

    private final ChartService chartService;
    private final JwtService jwtService;

    /**
     * 기간별 시세 조회
     */
    @GetMapping("/period-price")
    public BaseResponse<List<GetPeriodPriceRes>> getPeriodPrice(@RequestParam("AUTH") String auth,
                                                                @RequestParam("EXCD") String excd,
                                                                @RequestParam("SYMB") String symb,
                                                                @RequestParam("GUBN") String gubn,
                                                                @RequestParam("BYMD") String bymd,
                                                                @RequestParam("MODP") String modp) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(chartService.getPeriodPrice(userId, auth, excd, symb, gubn, bymd, modp));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 현재가 조회
     */
    @GetMapping("/current-price")
    public BaseResponse<GetCurrentPriceRes> getCurrentPrice(@RequestParam("AUTH") String auth,
                                                            @RequestParam("EXCD") String excd,
                                                            @RequestParam("SYMB") String symb) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(chartService.getCurrentPrice(userId, auth, excd, symb));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
