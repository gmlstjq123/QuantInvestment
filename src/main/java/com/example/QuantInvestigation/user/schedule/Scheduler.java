package com.example.QuantInvestigation.user.schedule;


import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.user.UserService;
import com.example.QuantInvestigation.user.dto.RefreshTokenReq;
import com.example.QuantInvestigation.utils.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class Scheduler {

    private final UserService userService;
    private final UtilService utilService;

    // @Scheduled(cron = "0 0 18 * * ?") // 매일 새벽 3시에 수행 (미국 기준 18시)
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul") // 매일 새벽 3시에 수행
    public void refreshTokens() { // 유저의 OAuth 토큰 Refresh
        log.info("<-------------모든 Token의 Refresh 작업을 수행합니다.------------->");
        List<RefreshTokenReq> refreshTokenReqList = utilService.findRefreshTokenReqAllWithValidation();
        userService.refreshToken(refreshTokenReqList);
        log.info("<-------------Token Refresh 작업이 완료되었습니다.------------->");
    }

    // @Scheduled(cron = "0 0 19 * * ?") // 매일 새벽 4시에 수행 (미국 기준 19시)
    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul") // 매일 새벽 4시에 수행
    public void automaticBuyAndSell() { // 매수, 매도 주문
        log.info("<-------------모든 User에 대해 침몰방지법 매매 작업을 수행합니다.------------->");
        userService.automaticBuyAndSell("CTCX");
        log.info("<-------------모든 User에 대해 침몰방지법 매매가 완료되었습니다.------------->");
    }

    // @Scheduled(cron = "0 0 22 * * ?") // 매일 아침 7시에 수행 (미국 기준 22시)
    @Scheduled(cron = "0 0 7 * * ?", zone = "Asia/Seoul") // 매일 아침 7시에 수행
    public void checkConclusion() { // 체결 결과 확인 & T값 및 최대 보유 일수 갱신
        log.info("<-------------매수/매도 체결 여부를 갱신합니다.------------->");
        userService.checkConclusion("CTCX");
        log.info("<-------------매수/매도 체결 여부를 갱신합니다.------------->");
    }

}
