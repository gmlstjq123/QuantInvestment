package com.example.QuantInvestigation.user;

import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.error_log.ErrorLog;
import com.example.QuantInvestigation.error_log.ErrorLogRepository;
import com.example.QuantInvestigation.token.JwtProvider;
import com.example.QuantInvestigation.token.dto.JwtResponseDTO;
import com.example.QuantInvestigation.user.dto.*;
import com.example.QuantInvestigation.utils.AES128;
import com.example.QuantInvestigation.utils.Secret;
import com.example.QuantInvestigation.utils.UtilService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.QuantInvestigation.exception.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UtilService utilService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final ErrorLogRepository errorLogRepository;

    @Transactional
    public String joinUser(PostJoinReq postJoinReq) throws BaseException {

        String id = postJoinReq.getId();
        String pwd = postJoinReq.getPassword();
        String accountNum = postJoinReq.getAccountNum();
        String appKey = postJoinReq.getAppKey();
        String appSecret = postJoinReq.getAppSecret();

        RestTemplate restTemplate = new RestTemplate();
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent", "Mozilla/5.0");

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", appKey);
        body.put("appsecret", appSecret);
        String accessToken = "";

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://openapi.koreainvestment.com:9443/oauth2/tokenP",  // 호출할 API의 URL
                    HttpMethod.POST, // 요청 방법 (GET, POST 등)
                    requestEntity, // 요청에 대한 데이터 (필요에 따라 설정)
                    String.class
            );

            String tokenInfo = responseEntity.getBody();
            // JSON 데이터에서 필요한 정보 추출
            Gson gsonObj = new Gson();
            Map<?, ?> data = gsonObj.fromJson(tokenInfo, Map.class);
            accessToken = (String) data.get("access_token");

        } catch (Exception ignored) {
            throw new BaseException(INVALID_AUTH_INPUT);
        }

        try{
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(pwd);
        }
        catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        User user = new User();
        user.createUser(id, pwd, appKey, appSecret, accessToken, accountNum);
        userRepository.save(user);

        return "회원 가입이 완료되었습니다.";
    }

    @Transactional
    public String loginUser(PostLoginReq postLoginReq) throws BaseException {
        String id = postLoginReq.getId();
        String pw = postLoginReq.getPassword();
        User user = utilService.findByIdWithValidation(id);

        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (pw.equals(password)) {
            JwtResponseDTO.TokenInfo tokenInfo = jwtProvider.generateToken(user.getUserId());
            return tokenInfo.getAccessToken();
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    @Transactional
    public String getAccountNumber(Long userId) throws BaseException {
        String accountNumber = utilService.findAccountNumByUserIdWithValidation(userId);
        log.info("계좌번호: " + accountNumber);
        if (accountNumber.length() == 10) {
            return accountNumber.substring(0, 8) + "-" + accountNumber.substring(8);
        } else {
            throw new BaseException(INVALID_ACCOUNT);
        }
    }

    /**
     * OAuth 토큰 전체 Refresh
     */
    @Transactional
    public void refreshToken(List<RefreshTokenReq> refreshTokenReqList) throws BaseException {
        for (RefreshTokenReq refreshTokenReq : refreshTokenReqList) {
            Long userId = refreshTokenReq.getUserId();
            String appKey = refreshTokenReq.getAppKey();
            String appSecret = refreshTokenReq.getAppSecret();

            RestTemplate restTemplate = new RestTemplate();
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("User-Agent", "Mozilla/5.0");

            // 요청 바디 설정
            Map<String, String> body = new HashMap<>();
            body.put("grant_type", "client_credentials");
            body.put("appkey", appKey);
            body.put("appsecret", appSecret);
            String accessToken = "";

            try {
                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        "https://openapi.koreainvestment.com:9443/oauth2/tokenP",  // 호출할 API의 URL
                        HttpMethod.POST, // 요청 방법 (GET, POST 등)
                        requestEntity, // 요청에 대한 데이터 (필요에 따라 설정)
                        String.class
                );

                String tokenInfo = responseEntity.getBody();
                // JSON 데이터에서 필요한 정보 추출
                Gson gsonObj = new Gson();
                Map<?, ?> data = gsonObj.fromJson(tokenInfo, Map.class);
                accessToken = (String) data.get("access_token");
                userRepository.updateAccessTokenByUserId(userId, accessToken);
                System.out.println(userId + "의 액세스 토큰: " + accessToken);

            } catch (Exception ignored) {
                User user = utilService.findByUserIdWithValidation(userId);
                ErrorLog errorLog = new ErrorLog();
                errorLog.createHistory("OAuth 토큰 갱신에 실패하였습니다.", user);
                errorLogRepository.save(errorLog);

                throw new BaseException(INVALID_AUTH_INPUT);
            }
        }
    }

    @Transactional
    public GetTotalBalanceRes getTotalBalance(Long userId) throws BaseException {
        String accessToken = utilService.findTokenByUserIdWithValidation(userId);
        String appKey = utilService.findAppKeyByUserIdWithValidation(userId);
        String appSecret = utilService.findAppSecretByUserIdWithValidation(userId);
        String accountNumber = utilService.findAccountNumByUserIdWithValidation(userId);
        String firstPart = accountNumber.substring(0, 8); // 계좌번호 첫 8자리
        String lastPart = accountNumber.substring(8); // 계좌번호 나머지 2자리
        String trId = "TTTS3012R";

        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("authorization", "Bearer " + accessToken);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", trId);
        headers.add("User-Agent", "Mozilla/5.0");

        // 파라미터 설정
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", firstPart);
        queryParams.add("ACNT_PRDT_CD", lastPart);
        queryParams.add("OVRS_EXCG_CD", "NASD");
        queryParams.add("TR_CRCY_CD", "USD");
        queryParams.add("CTX_AREA_FK200", "");
        queryParams.add("CTX_AREA_NK200", "");

        String url = "https://openapi.koreainvestment.com:9443/uapi/overseas-stock/v1/trading/inquire-balance";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);
        String finalUrl = builder.toUriString();

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    finalUrl,  // 호출할 API의 URL
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode output2 = rootNode.path("output2");

            return new GetTotalBalanceRes(
                    Float.parseFloat(output2.path("ovrs_tot_pfls").asText()),
                    Float.parseFloat(output2.path("ovrs_rlzt_pfls_amt").asText()),
                    Float.parseFloat(output2.path("rlzt_erng_rt").asText()),
                    Float.parseFloat(output2.path("tot_evlu_pfls_amt").asText()),
                    Float.parseFloat(output2.path("tot_pftrt").asText())
            );

        } catch (Exception e) {
            throw new BaseException(INVALID_PARAMS);
        }
    }

    @Transactional
    public GetItemBalanceRes getItemBalance(Long userId) throws BaseException {
        String accessToken = utilService.findTokenByUserIdWithValidation(userId);
        String appKey = utilService.findAppKeyByUserIdWithValidation(userId);
        String appSecret = utilService.findAppSecretByUserIdWithValidation(userId);
        String accountNumber = utilService.findAccountNumByUserIdWithValidation(userId);
        String firstPart = accountNumber.substring(0, 8); // 계좌번호 첫 8자리
        String lastPart = accountNumber.substring(8); // 계좌번호 나머지 2자리
        String trId = "TTTS3012R";

        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("authorization", "Bearer " + accessToken);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", trId);
        headers.add("User-Agent", "Mozilla/5.0");

        // 파라미터 설정
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("CANO", firstPart);
        queryParams.add("ACNT_PRDT_CD", lastPart);
        queryParams.add("OVRS_EXCG_CD", "NASD");
        queryParams.add("TR_CRCY_CD", "USD");
        queryParams.add("CTX_AREA_FK200", "");
        queryParams.add("CTX_AREA_NK200", "");
        String url = "https://openapi.koreainvestment.com:9443/uapi/overseas-stock/v1/trading/inquire-balance";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);
        String finalUrl = builder.toUriString();

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // TODO: output1이 비어있을 때(보유 수량이 0일 때) 또는 종목 수가 2 이상인 경우에 대한 예외처리
//            String responseBody = responseEntity.getBody();
//            JsonNode rootNode = objectMapper.readTree(responseBody);
//            JsonNode outputArray = rootNode.path("output1");
//            JsonNode targetNode = null;
//            for (JsonNode node : outputArray) {
//                if ("TQQQ".equals(node.path("ovrs_pdno").asText())) {
//                    targetNode = node;
//                    break;
//                }
//            }
//
//            if (targetNode != null) {
//                return new GetItemBalanceRes(
//                        targetNode.path("ovrs_pdno").asText(),
//                        Float.parseFloat(targetNode.path("frcr_evlu_pfls_amt").asText()),
//                        Float.parseFloat(targetNode.path("evlu_pfls_rt").asText()),
//                        Float.parseFloat(targetNode.path("pchs_avg_pric").asText()),
//                        Integer.parseInt(targetNode.path("ovrs_cblc_qty").asText()),
//                        Float.parseFloat(targetNode.path("ovrs_stck_evlu_amt").asText())
//                );
//            } else {
//                return new GetItemBalanceRes("TQQQ", 0F, 0F, 0F, 0, 0F);
//            }

            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode output1 = rootNode.path("output1").get(0);
            log.info(String.valueOf(output1));
            return new GetItemBalanceRes(
                    output1.path("ovrs_pdno").asText(),
                    Float.parseFloat(output1.path("frcr_evlu_pfls_amt").asText()),
                    Float.parseFloat(output1.path("evlu_pfls_rt").asText()),
                    Float.parseFloat(output1.path("pchs_avg_pric").asText()),
                    Integer.parseInt(output1.path("ovrs_cblc_qty").asText()),
                    Float.parseFloat(output1.path("ovrs_stck_evlu_amt").asText())
            );

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BaseException(INVALID_PARAMS);
        }
    }

    /**
     * 주식 매수 주문
     */
    @Transactional
    public String buyOrder(Long userId, String purchasePrice, Integer qty) throws BaseException {
        String accessToken = utilService.findTokenByUserIdWithValidation(userId);
        String appKey = utilService.findAppKeyByUserIdWithValidation(userId);
        String appSecret = utilService.findAppSecretByUserIdWithValidation(userId);
        String accountNumber = utilService.findAccountNumByUserIdWithValidation(userId);
        String firstPart = accountNumber.substring(0, 8); // 계좌번호 첫 8자리
        String lastPart = accountNumber.substring(8); // 계좌번호 나머지 2자리
        String trId = "TTTT1002U";

        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("authorization", "Bearer " + accessToken);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", trId);
        headers.add("User-Agent", "Mozilla/5.0");

        // 바디 설정 (JSON으로 변환)
        Map<String, Object> body = new HashMap<>();
        body.put("CANO", firstPart);
        body.put("ACNT_PRDT_CD", lastPart);
        body.put("OVRS_EXCG_CD", "NASD");
        body.put("PDNO", "TQQQ");
        body.put("ORD_QTY", qty.toString());
        body.put("OVRS_ORD_UNPR", purchasePrice); // 1주당 가격
        body.put("ORD_SVR_DVSN_CD", "0");
        body.put("ORD_DVSN", "34");

        // ObjectMapper를 사용하여 body를 JSON 문자열로 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://openapi.koreainvestment.com:9443/uapi/overseas-stock/v1/trading/order",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode rtCd = rootNode.path("rt_cd");
            JsonNode msg1 = rootNode.path("msg1");

            if (rtCd.asInt() == 0) { // 성공 처리
                log.info(msg1.asText());
            } else if (rtCd.asInt() == 7) { // 휴장일

            } else { // 실패 처리
                User user = utilService.findByUserIdWithValidation(userId);
                ErrorLog errorLog = new ErrorLog();
                errorLog.createHistory("매수에 실패하였습니다. 사유:" + msg1.asText(), user);
                errorLogRepository.save(errorLog);
            }

            return msg1.asText();

        } catch (Exception e) {
            User user = utilService.findByUserIdWithValidation(userId);
            ErrorLog errorLog = new ErrorLog();
            errorLog.createHistory("매수에 실패하였습니다. 사유:" + e.getMessage(), user);
            errorLogRepository.save(errorLog);
            log.info(e.getMessage());
            throw new BaseException(INVALID_PARAMS);
        }
    }

    /**
     * 주식 매도 주문
     */
    @Transactional
    public String sellOrder(Long userId, String sellingPrice, Integer qty) throws BaseException {
        String accessToken = utilService.findTokenByUserIdWithValidation(userId);
        String appKey = utilService.findAppKeyByUserIdWithValidation(userId);
        String appSecret = utilService.findAppSecretByUserIdWithValidation(userId);
        String accountNumber = utilService.findAccountNumByUserIdWithValidation(userId);
        String firstPart = accountNumber.substring(0, 8); // 계좌번호 첫 8자리
        String lastPart = accountNumber.substring(8); // 계좌번호 나머지 2자리
        String trId = "TTTT1006U";

        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("authorization", "Bearer " + accessToken);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", trId);
        headers.add("User-Agent", "Mozilla/5.0");

        // 바디 설정 (JSON으로 변환)
        Map<String, Object> body = new HashMap<>();
        body.put("CANO", firstPart);
        body.put("ACNT_PRDT_CD", lastPart);
        body.put("OVRS_EXCG_CD", "NASD");
        body.put("PDNO", "TQQQ");
        body.put("ORD_QTY", qty.toString());
        body.put("OVRS_ORD_UNPR", sellingPrice); // 1주당 가격
        body.put("SLL_TYPE", "00");
        body.put("ORD_SVR_DVSN_CD", "0");
        body.put("ORD_DVSN", "34");

        // ObjectMapper를 사용하여 body를 JSON 문자열로 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://openapi.koreainvestment.com:9443/uapi/overseas-stock/v1/trading/order",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode rtCd = rootNode.path("rt_cd");
            JsonNode msg1 = rootNode.path("msg1");

            if (rtCd.asInt() == 0) { // 성공 처리
                log.info(msg1.asText());
            } else if (rtCd.asInt() == 7) { // 휴장일

            } else { // 실패 처리
                User user = utilService.findByUserIdWithValidation(userId);
                ErrorLog errorLog = new ErrorLog();
                errorLog.createHistory("매도에 실패하였습니다. 사유:" + msg1.asText(), user);
                errorLogRepository.save(errorLog);
            }

            return msg1.asText();

        } catch (Exception e) {
            User user = utilService.findByUserIdWithValidation(userId);
            ErrorLog errorLog = new ErrorLog();
            errorLog.createHistory("매도에 실패하였습니다. 사유:" + e.getMessage(), user);
            errorLogRepository.save(errorLog);
            log.info(e.getMessage());
            throw new BaseException(INVALID_PARAMS);
        }
    }

}