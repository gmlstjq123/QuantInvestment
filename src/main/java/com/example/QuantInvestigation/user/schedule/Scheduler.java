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

    @Scheduled(cron = "0 0 20 * * ?") // 매일 밤 8시에 수행
    public void refreshTokens() {
        log.info("<-------------모든 Token의 Refresh 작업을 수행합니다.------------->");
        List<RefreshTokenReq> refreshTokenReqList = utilService.findRefreshTokenReqAllWithValidation();
        userService.refreshToken(refreshTokenReqList);
        log.info("<-------------Token Refresh 작업이 완료되었습니다.------------->");
    }

}
