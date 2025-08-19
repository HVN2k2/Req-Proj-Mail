package com.hvn_proj_mail.mail.project.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class DovecotMailDTO {
    private String raw_mail;
    private String timestamp;
    private String source;
    private String processing_time;

    @JsonProperty("parsed_info")
    private ParsedMailInfo parsed_info;

    @Data
    public static class ParsedMailInfo {
        private String from;
        private String to;
        private String subject;
        private String date;
        private String message_id;
        private String content_type;
        private String body;
        private int attachments_count;
        private long size;
    }
}