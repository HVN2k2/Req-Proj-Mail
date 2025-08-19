package com.hvn_proj_mail.mail.project.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailDTO {
    // Không cần from vì sẽ lấy từ current user
    private String to;
    private String subject;
    private String message;
}