package com.hvn_proj_mail.mail.project.controller;

import com.hvn_proj_mail.mail.project.request.AuthResponse;
import com.hvn_proj_mail.mail.project.request.LoginRequest;
import com.hvn_proj_mail.mail.project.request.RegisterRequest;
import com.hvn_proj_mail.mail.project.response.LaoIdResponse;
import com.hvn_proj_mail.mail.project.service.AuthService;
import com.hvn_proj_mail.mail.project.service.JwtService;
import com.hvn_proj_mail.mail.project.service.LaoIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final LaoIdService laoIdService;
    private final JwtService jwtService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "Email and password are required"));
        }

        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid email or password: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/laoid", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> handleLaoIdLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "Missing authorization code"));
        }

        try {
            LaoIdResponse tokenResponse = laoIdService.getAccessToken(code);
            if (tokenResponse == null || !tokenResponse.isSuccess() || tokenResponse.getData() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, tokenResponse != null && tokenResponse.getMessage() != null ?
                                tokenResponse.getMessage() : "Failed to get access token"));
            }

            String accessToken = tokenResponse.getData().getAccessToken();
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Access token is null"));
            }

            LaoIdResponse userResponse = laoIdService.getUserInfo(accessToken);
            if (userResponse == null || !userResponse.isSuccess() || userResponse.getData() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, userResponse != null && userResponse.getMessage() != null ?
                                userResponse.getMessage() : "Failed to get user info"));
            }

            LaoIdResponse.Data laoIdUser = userResponse.getData();
            AuthResponse authResponse = authService.saveLaoIdUser(laoIdUser);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "LaoID login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/laoid/callback")
    public ResponseEntity<AuthResponse> handleLaoIdCallback(@RequestParam("authorization_code") String code) {
        System.out.println("=== DEBUG: handleLaoIdCallback ===");
        System.out.println("Authorization code: " + code);

        if (code == null || code.isEmpty()) {
            System.out.println("ERROR: Missing authorization code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "Missing authorization code"));
        }

        try {
            System.out.println("Calling laoIdService.getAccessToken...");
            LaoIdResponse tokenResponse = laoIdService.getAccessToken(code);
            System.out.println("Token response: " + tokenResponse);

            if (tokenResponse == null || !tokenResponse.isSuccess() || tokenResponse.getData() == null) {
                System.out.println("ERROR: Failed to get access token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, tokenResponse != null && tokenResponse.getMessage() != null ?
                                tokenResponse.getMessage() : "Failed to get access token"));
            }

            String accessToken = tokenResponse.getData().getAccessToken();
            System.out.println("Access token: " + accessToken);

            if (accessToken == null) {
                System.out.println("ERROR: Access token is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Access token is null"));
            }

            System.out.println("Calling laoIdService.getUserInfo...");
            LaoIdResponse userResponse = laoIdService.getUserInfo(accessToken);
            System.out.println("User response: " + userResponse);
            System.out.println("User response success: " + (userResponse != null ? userResponse.isSuccess() : "null"));
            System.out.println("User response data: " + (userResponse != null ? userResponse.getData() : "null"));

            if (userResponse == null || !userResponse.isSuccess() || userResponse.getData() == null) {
                System.out.println("ERROR: Failed to get user info");
                System.out.println("UserResponse is null: " + (userResponse == null));
                System.out.println("UserResponse success: " + (userResponse != null ? userResponse.isSuccess() : "N/A"));
                System.out.println("UserResponse data is null: " + (userResponse != null ? (userResponse.getData() == null) : "N/A"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, userResponse != null && userResponse.getMessage() != null ?
                                userResponse.getMessage() : "Failed to get user info"));
            }

            LaoIdResponse.Data laoIdUser = userResponse.getData();
            System.out.println("LaoIdUser: " + laoIdUser);

            System.out.println("Calling authService.saveLaoIdUser...");
            AuthResponse authResponse = authService.saveLaoIdUser(laoIdUser);
            System.out.println("AuthResponse: " + authResponse);
            System.out.println("=== END DEBUG ===");

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            System.out.println("ERROR: Exception in handleLaoIdCallback: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "LaoID login failed: " + e.getMessage()));
        }
    }
}