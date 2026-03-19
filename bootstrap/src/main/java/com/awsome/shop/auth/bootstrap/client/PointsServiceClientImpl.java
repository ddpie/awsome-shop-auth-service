package com.awsome.shop.auth.bootstrap.client;

import com.awsome.shop.auth.application.api.client.PointsServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 积分服务客户端实现（HTTP 调用 points-service 内部接口）
 */
@Slf4j
@Component
public class PointsServiceClientImpl implements PointsServiceClient {

    private final RestTemplate restTemplate;
    private final String pointsServiceUrl;

    public PointsServiceClientImpl(
            RestTemplate restTemplate,
            @Value("${service.points.url:http://localhost:8003}") String pointsServiceUrl) {
        this.restTemplate = restTemplate;
        this.pointsServiceUrl = pointsServiceUrl;
    }

    @Override
    public void initPoints(Long userId, int initialBalance) {
        String url = pointsServiceUrl + "/api/v1/internal/point/init";
        Map<String, Object> body = Map.of("userId", userId, "initialBalance", initialBalance);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !"SUCCESS".equals(responseBody.get("code"))) {
                log.warn("[PointsClient] 初始化积分失败, userId={}, response={}", userId, responseBody);
            } else {
                log.info("[PointsClient] 初始化积分成功, userId={}, balance={}", userId, initialBalance);
            }
        } catch (Exception e) {
            log.error("[PointsClient] 初始化积分异常, userId={}", userId, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Integer getBalance(Long userId) {
        String url = pointsServiceUrl + "/api/v1/internal/point/balance";
        Map<String, Object> body = Map.of("userId", userId);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !"SUCCESS".equals(responseBody.get("code"))) {
                log.warn("[PointsClient] 查询余额失败, userId={}, response={}", userId, responseBody);
                return null;
            }

            Object data = responseBody.get("data");
            if (data instanceof Map) {
                Object balance = ((Map<String, Object>) data).get("balance");
                return balance != null ? ((Number) balance).intValue() : null;
            }
            return null;
        } catch (ResourceAccessException e) {
            log.warn("[PointsClient] 查询余额超时, userId={}", userId, e);
            return null;
        } catch (Exception e) {
            log.error("[PointsClient] 查询余额异常, userId={}", userId, e);
            return null;
        }
    }
}
