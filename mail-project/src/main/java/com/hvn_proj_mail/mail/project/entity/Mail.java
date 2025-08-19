package com.hvn_proj_mail.mail.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thêm user_id để phân biệt mail của từng user
    @Column(name = "user_id")
    private String userId;

    private String sender;
    private String receiver;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private MailStatus status;

    private LocalDateTime createdAt;

    public enum MailStatus {
        SENT,
        RECEIVED,
        PENDING,
        FAILED
    }
}