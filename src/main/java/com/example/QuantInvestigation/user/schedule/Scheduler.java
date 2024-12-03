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

/**
 * 유저의 OAuth 토큰 Refresh
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class Scheduler {

    private final UserService userService;
    private final UtilService utilService;

    @Scheduled(cron = "0 0 18 * * ?") // 매일 새벽 3시에 수행
    public void refreshTokens() {
        log.info("<-------------모든 Token의 Refresh 작업을 수행합니다.------------->");
        List<RefreshTokenReq> refreshTokenReqList = utilService.findRefreshTokenReqAllWithValidation();
        userService.refreshToken(refreshTokenReqList);
        log.info("<-------------Token Refresh 작업이 완료되었습니다.------------->");
    }

    @Scheduled(cron = "0 0 19 * * ?") // 매일 새벽 4시에 수행
    public void automaticBuyAndSell() {
        log.info("<-------------모든 User에 대해 침몰방지법 매매 작업을 수행합니다.------------->");

        log.info("<-------------모든 User에 대해 침몰방지법 매매가 완료되었습니다.------------->");
    }

}
