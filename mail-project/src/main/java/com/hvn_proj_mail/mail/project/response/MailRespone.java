package com.hvn_proj_mail.mail.project.response;

import com.hvn_proj_mail.mail.project.until.MailEnumResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRespone<T> {
    String code;
    String message;
    T data;

    public MailRespone(T data, MailEnumResponse response) {
        this.data = data;
        this.code = response.getCode();
        this.message = response.getMessage();
    }
}
