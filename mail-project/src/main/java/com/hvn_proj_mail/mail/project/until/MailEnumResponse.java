package com.hvn_proj_mail.mail.project.until;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum MailEnumResponse {
    SUCCESS("200", "Mail handle succesfully!"),
    PENDING("201", "Mail pending!"),
    Fail("500", "Mail send fail!");

    String code;
    String message;

    MailEnumResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }

}
