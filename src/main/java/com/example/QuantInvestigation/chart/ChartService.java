package com.example.QuantInvestigation.chart;

import com.example.QuantInvestigation.chart.dto.GetCurrentPriceRes;
import com.example.QuantInvestigation.chart.dto.GetPeriodPriceRes;
import com.example.QuantInvestigation.error_log.ErrorLog;
import com.example.QuantInvestigation.error_log.ErrorLogRepository;
import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.utils.UtilService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.DecimalFormat;
import java.util.*;

import static com.example.QuantInvestigation.exception.BaseResponseStatus.INVALID_PARAMS;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChartService {

    private final UtilService utilService;
    private final ErrorLogRepository errorLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<GetPeriodPriceRes> getPeriodPrice(Long userId, String AUTH, String EXCD, String SYMB,
                                                  String GUBN, String BYMD, String MODP) throws BaseException {


        String accessToken = utilService.findTokenByUserIdWithValidation(userId);
        String appKey = utilService.findAppKeyByUserIdWithValidation(userId);
        String appSecret = utilService.findAppSecretByUserIdWithValidation(userId);
        String trId = "HHDFS76240000";

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
        queryParams.add("AUTH", AUTH);
        queryParams.add("EXCD", EXCD);
        queryParams.add("SYMB", SYMB);
        queryParams.add("GUBN", GUBN);
        queryParams.add("BYMD", BYMD);
        queryParams.add("MODP", MODP);

        String url = "https://openapi.koreainvestment.com:9443/uapi/overseas-price/v1/quotations/dailyprice";
        // 쿼리 파라미터가 추가된 URL 생성
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
            List<GetPeriodPriceRes> periodPriceList = new ArrayList<>();

            for (JsonNode dataNode : output2) {
                GetPeriodPriceRes periodPrice = new GetPeriodPriceRes(
                        dataNode.path("xymd").asText(), // 날짜
                        Float.parseFloat(dataNode.path("open").asText()), // 시가
                        Float.parseFloat(dataNode.path("high").asText()), // 고가
                        Float.parseFloat(dataNode.path("low").asText()), // 저가
                        Float.parseFloat(dataNode.path("clos").asText()), // 종가
                        Float.parseFloat(dataNode.path("rate").asText())  // 등락률
                );

                // 리스트에 추가
                periodPriceList.add(periodPrice);
            }

            Collections.reverse(periodPriceList);
            return periodPriceList;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            User user = utilService.findByUserIdWithValidation(userId);
            ErrorLog errorLog = new ErrorLog();
            errorLog.createErrorLog(e.getMessage(), user);
            errorLogRepository.save(errorLog);
            throw new BaseException(INVALID_PARAMS);
        }
    }

    @Transactional
    public GetCurrentPriceRes getCurrentPrice(Long userId, String AUTH, String EXCD, String SYMB) throws BaseException {
        String accessToken = utilService.findTokenByUserIdWithValidation(userId);
        String appKey = utilService.findAppKeyByUserIdWithValidation(userId);
        String appSecret = utilService.findAppSecretByUserIdWithValidation(userId);
        String trId = "HHDFS00000300";

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
        queryParams.add("AUTH", AUTH);
        queryParams.add("EXCD", EXCD);
        queryParams.add("SYMB", SYMB);

        String url = "https://openapi.koreainvestment.com:9443/uapi/overseas-price/v1/quotations/price";
        // 쿼리 파라미터가 추가된 URL 생성
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
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the JSON response as a tree
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Navigate to the 'output' node
            JsonNode outputNode = rootNode.path("output");

            // Extract values
            float last = Float.parseFloat(outputNode.path("last").asText());
            float rate = Float.parseFloat(outputNode.path("rate").asText());
            float diff = Float.parseFloat(outputNode.path("diff").asText());

            DecimalFormat df = new DecimalFormat("#.00"); // Two decimal places

            String formattedLast = df.format(last);
            String formattedRate = df.format(rate);
            String formattedDiff = df.format(diff);

            last = Float.parseFloat(formattedLast);
            rate = Float.parseFloat(formattedRate);
            diff = Float.parseFloat(formattedDiff);

            return new GetCurrentPriceRes(last, diff, rate);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            User user = utilService.findByUserIdWithValidation(userId);
            ErrorLog errorLog = new ErrorLog();
            errorLog.createErrorLog(e.getMessage(), user);
            errorLogRepository.save(errorLog);
            throw new BaseException(INVALID_PARAMS);
        }
    }
}