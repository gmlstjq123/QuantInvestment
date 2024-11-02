package com.example.QuantInvestigation.user;

import com.example.QuantInvestigation.account.AccountRepository;
import com.example.QuantInvestigation.chart.dto.GetPeriodPriceRes;
import com.example.QuantInvestigation.exception.BaseException;
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
import org.springframework.stereotype.Component;
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

            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode output1 = rootNode.path("output1").get(0);;
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


}