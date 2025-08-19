package com.hvn_proj_mail.mail.project.service;

import com.hvn_proj_mail.mail.project.response.LaoIdResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LaoIdService {
    @Value("${laoid.client-id}")
    private String clientId;

    @Value("${laoid.client-secret}")
    private String clientSecret;

    // Giữ nguyên URL demo
    private final String verifyUrl = "https://demo-sso.tinasoft.io/api/v1/third-party/verify";
    private final String userInfoUrl = "https://demo-sso.tinasoft.io/api/v1/third-party/me";

    private final RestTemplate restTemplate = new RestTemplate();

    public LaoIdResponse getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Accept-Language", "vi");
        headers.set("Content-Type", "application/json");

        // Cập nhật request body theo tài liệu
        String body = String.format("{\"code\":\"%s\",\"clientId\":\"%s\",\"clientSecret\":\"%s\"}",
                code, clientId, clientSecret);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<LaoIdResponse> response = restTemplate.postForEntity(verifyUrl, request, LaoIdResponse.class);
        return response.getBody();
    }

    public LaoIdResponse getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Accept-Language", "vi");
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("x-api-key", clientId); // Sử dụng clientId làm x-api-key

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<LaoIdResponse> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, LaoIdResponse.class);
        return response.getBody();
    }
}