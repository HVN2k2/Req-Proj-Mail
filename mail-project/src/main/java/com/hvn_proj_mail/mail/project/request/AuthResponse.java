package com.hvn_proj_mail.mail.project.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String message;
//    public AuthResponse(String token) {
//        this.token = token;
//        this.message = "Login successful"; // Default message
//    }

    // Constructor for token and message (e.g., error cases)
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
}