package com.hvn_proj_mail.mail.project.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String name;
    private String password;
}