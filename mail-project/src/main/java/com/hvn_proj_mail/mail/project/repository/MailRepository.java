package com.hvn_proj_mail.mail.project.repository;

import com.hvn_proj_mail.mail.project.entity.Mail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {

    // Lấy tất cả mail đã nhận (inbox)
    @Query("SELECT m FROM Mail m WHERE m.receiver = :email AND m.status = 'RECEIVED' ORDER BY m.createdAt DESC")
    Page<Mail> findInboxByEmail(@Param("email") String email, Pageable pageable);

    // Lấy tất cả mail đã gửi (sent)
    @Query("SELECT m FROM Mail m WHERE m.sender = :email AND m.status = 'SENT' ORDER BY m.createdAt DESC")
    Page<Mail> findSentByEmail(@Param("email") String email, Pageable pageable);

    // Lấy tất cả mail nháp (drafts) - có thể dựa vào status hoặc logic khác
    @Query("SELECT m FROM Mail m WHERE m.sender = :email AND m.status = 'PENDING' ORDER BY m.createdAt DESC")
    Page<Mail> findDraftsByEmail(@Param("email") String email, Pageable pageable);

    // Lấy tất cả mail trong thùng rác (trash) - có thể thêm field isDeleted
    @Query("SELECT m FROM Mail m WHERE (m.sender = :email OR m.receiver = :email) AND m.status = 'FAILED' ORDER BY m.createdAt DESC")
    Page<Mail> findTrashByEmail(@Param("email") String email, Pageable pageable);

    // Tìm kiếm mail theo từ khóa
    @Query("SELECT m FROM Mail m WHERE (m.sender = :email OR m.receiver = :email) AND " +
           "(LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.createdAt DESC")
    Page<Mail> searchMailsByKeyword(@Param("email") String email, @Param("keyword") String keyword, Pageable pageable);

    // Lấy mail theo receiver email (public endpoint)
    List<Mail> findByReceiverOrderByCreatedAtDesc(String receiver);

    // Đếm số mail chưa đọc (có thể thêm field isRead)
    @Query("SELECT COUNT(m) FROM Mail m WHERE m.receiver = :email AND m.status = 'RECEIVED'")
    long countUnreadMails(@Param("email") String email);

    // Query theo user_id để phân biệt mail của từng user
    List<Mail> findByUserIdAndStatus(String userId, Mail.MailStatus status);
    Page<Mail> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Page<Mail> findByUserIdAndReceiverOrderByCreatedAtDesc(String userId, String receiver, Pageable pageable);

    // Methods cũ (để backward compatibility)
    List<Mail> findByStatus(Mail.MailStatus status);
    Page<Mail> findByReceiverOrderByCreatedAtDesc(String receiver, Pageable pageable);
    Page<Mail> findByReceiverAndStatusOrderByCreatedAtDesc(String receiver, Mail.MailStatus status, Pageable pageable);
    
    // Kiểm tra mail đã tồn tại dựa trên sender, receiver, subject và thời gian gần nhau
    @Query("SELECT COUNT(m) > 0 FROM Mail m WHERE m.sender = :sender AND m.receiver = :receiver " +
           "AND m.subject = :subject AND m.userId = :userId " +
           "AND m.createdAt BETWEEN :startTime AND :endTime") // Trong khoảng thời gian 60 giây
    boolean existsBySenderReceiverSubjectAndTime(@Param("sender") String sender, 
                                                @Param("receiver") String receiver, 
                                                @Param("subject") String subject,
                                                @Param("userId") String userId,
                                                @Param("startTime") java.time.LocalDateTime startTime,
                                                @Param("endTime") java.time.LocalDateTime endTime);
    
    // Kiểm tra mail đã tồn tại dựa trên message ID (nếu có)
    @Query("SELECT COUNT(m) > 0 FROM Mail m WHERE m.sender = :sender AND m.receiver = :receiver " +
           "AND m.subject = :subject AND m.message = :message AND m.userId = :userId")
    boolean existsBySenderReceiverSubjectAndMessage(@Param("sender") String sender, 
                                                   @Param("receiver") String receiver, 
                                                   @Param("subject") String subject,
                                                   @Param("message") String message,
                                                   @Param("userId") String userId);
}