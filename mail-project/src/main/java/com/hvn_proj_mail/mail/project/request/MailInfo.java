package com.hvn_proj_mail.mail.project.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfo {
    private String from;
    private String to;
    private String subject;
    private String body;
    private String date;
    private String messageId;
    private String contentType;
    private int attachmentsCount;
    private long size;
}
