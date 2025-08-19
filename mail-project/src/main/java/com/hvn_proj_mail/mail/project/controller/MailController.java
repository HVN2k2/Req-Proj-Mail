package com.hvn_proj_mail.mail.project.controller;

import com.hvn_proj_mail.mail.project.entity.Mail;
import com.hvn_proj_mail.mail.project.exception.MailAlreadyExistsException;
import com.hvn_proj_mail.mail.project.request.MailDTO;
import com.hvn_proj_mail.mail.project.response.MailRespone;
import com.hvn_proj_mail.mail.project.response.PageResponse;
import com.hvn_proj_mail.mail.project.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Slf4j
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<MailRespone<MailDTO>> sendMail(@RequestBody MailDTO request) {
        try {
            mailService.sendMail(request);
            return ResponseEntity.ok(new MailRespone<>("200", "Mail handled", request));
        }
        catch (MailAlreadyExistsException e){
            log.warn("📧 Mail already exists: {}", e.getMessage());
            return ResponseEntity.status(409).body(new MailRespone<>("409", "Mail already exists: " + e.getMessage(), request));
        }
        catch (Exception e){
            log.error("❌ Error sending mail: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MailRespone<>("400", e.getMessage(), request));
        }
    }

    /**
     * Test gửi mail không cần authentication (cho development)
     */
    @PostMapping("/test-send")
    public ResponseEntity<Map<String, Object>> testSendMail(@RequestBody MailDTO request) {
        log.info("📧 Testing send mail: {}", request);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test đơn giản - chỉ trả về thông tin request
            response.put("success", true);
            response.put("message", "Test endpoint working");
            response.put("request", request);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ Test endpoint working - Request: {}", request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error in test endpoint: {}", e.getMessage());
            
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/getAllMail")
    public ResponseEntity<?> getInbox(
            @RequestParam String toEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Mail> result = mailService.getInbox(toEmail, page, size);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Mail>> getSentMails() {
        return ResponseEntity.ok(mailService.getSentMails());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Mail>> getPendingMails() {
        return ResponseEntity.ok(mailService.getPendingMails());
    }

    @GetMapping("/recived")
    public ResponseEntity<List<Mail>> getRecivedMails() {
        return ResponseEntity.ok(mailService.getReceivedMails());
    }

    @GetMapping("/failed")
    public ResponseEntity<List<Mail>> getFailedMails() {
        return ResponseEntity.ok(mailService.getFailedMails());
    }

    @DeleteMapping("/{id}")
    public String deleteMail(@PathVariable Long id) {
        mailService.deleteMail(id);
        return "Mail deleted";
    }

    /**
     * Lấy chi tiết mail theo ID
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getMailDetail(@PathVariable Long id) {
        log.info("🔍 Getting mail detail for ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            Mail mail = mailService.getMailByIdPublic(id);
            if (mail == null) {
                response.put("success", false);
                response.put("message", "Mail not found");
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("success", true);
            response.put("mail", mail);
            response.put("timestamp", System.currentTimeMillis());
            log.info("✅ Mail detail retrieved for ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error getting mail detail for ID {}: {}", id, e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ========== IMAP INTEGRATION ENDPOINTS ==========

    /**
     * Test kết nối IMAP
     */
    @GetMapping("/test-imap")
    public ResponseEntity<Map<String, Object>> testImapConnection() {
        log.info("🔍 Testing IMAP connection...");

        Map<String, Object> response = new HashMap<>();

        try {
            // Thêm thông tin cấu hình vào response
            response.put("config", Map.of(
                "host", "localhost",
                "port", 143,
                "username", "testuser",
                "ssl", false
            ));

            boolean isConnected = mailService.testImapConnection();

            response.put("success", isConnected);
            response.put("message", isConnected ? "IMAP connection successful" : "IMAP connection failed");
            response.put("timestamp", System.currentTimeMillis());

            if (isConnected) {
                log.info("✅ IMAP connection test successful");
                return ResponseEntity.ok(response);
            } else {
                log.warn("⚠️ IMAP connection test failed");
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Error testing IMAP connection: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error testing IMAP connection: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Bắt đầu monitoring mail
     */
    @PostMapping("/start-monitoring")
    public ResponseEntity<Map<String, Object>> startMailMonitoring() {
        log.info("🚀 Starting mail monitoring...");

        Map<String, Object> response = new HashMap<>();

        try {
            mailService.startMailMonitoring();

            response.put("success", true);
            response.put("message", "Mail monitoring started successfully");
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Mail monitoring started");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error starting mail monitoring: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error starting mail monitoring: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Dừng monitoring mail
     */
    @PostMapping("/stop-monitoring")
    public ResponseEntity<Map<String, Object>> stopMailMonitoring() {
        log.info("🛑 Stopping mail monitoring...");

        Map<String, Object> response = new HashMap<>();

        try {
            mailService.stopMailMonitoring();

            response.put("success", true);
            response.put("message", "Mail monitoring stopped successfully");
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Mail monitoring stopped");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error stopping mail monitoring: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error stopping mail monitoring: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Fetch mail mới ngay lập tức
     */
    @PostMapping("/fetch-now")
    public ResponseEntity<Map<String, Object>> fetchMailsNow() {
        log.info("📧 Fetching mails now...");

        Map<String, Object> response = new HashMap<>();

        try {
            mailService.fetchNewMails();

            response.put("success", true);
            response.put("message", "Mail fetching completed");
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Mail fetching completed");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error fetching mails: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error fetching mails: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Fetch TẤT CẢ mail (bao gồm cả đã đọc)
     */
    @PostMapping("/fetch-all")
    public ResponseEntity<Map<String, Object>> fetchAllMails() {
        log.info("📧 Fetching ALL mails...");

        Map<String, Object> response = new HashMap<>();

        try {
            int count = mailService.fetchAllMails();

            response.put("success", true);
            response.put("message", "All mail fetching completed");
            response.put("mailsFound", count);
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ All mail fetching completed - Found {} mails", count);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error fetching all mails: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error fetching all mails: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Fetch mail cho một tài khoản cụ thể
     */
    @PostMapping("/fetch-account/{username}")
    public ResponseEntity<Map<String, Object>> fetchMailsForAccount(@PathVariable String username) {
        log.info("🔄 Fetching mails for account: {}", username);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Tìm tài khoản trong cấu hình
            var account = mailService.getImapConfig().getAccountByEmail(username + "@hvn.com");
            if (account == null) {
                response.put("success", false);
                response.put("message", "Account not found: " + username);
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(404).body(response);
            }
            
            mailService.fetchNewMailsForAccount(account.getUsername(), account.getPassword());
            response.put("success", true);
            response.put("message", "Mail fetching completed for account: " + username);
            response.put("account", username);
            response.put("timestamp", System.currentTimeMillis());
            log.info("✅ Mail fetching completed for account: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error fetching mails for account {}: {}", username, e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("account", username);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Fetch mail cho user cụ thể (cho admin)
     */
    @PostMapping("/fetch-user/{userEmail}")
    public ResponseEntity<Map<String, Object>> fetchMailsForUser(@PathVariable String userEmail) {
        log.info("🔄 Fetching mails for user: {}", userEmail);
        Map<String, Object> response = new HashMap<>();
        
        try {
            mailService.fetchMailsForUser(userEmail);
            response.put("success", true);
            response.put("message", "Mail fetching completed for user: " + userEmail);
            response.put("user", userEmail);
            response.put("timestamp", System.currentTimeMillis());
            log.info("✅ Mail fetching completed for user: {}", userEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error fetching mails for user {}: {}", userEmail, e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("user", userEmail);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Lấy danh sách tài khoản Dovecot
     */
    @GetMapping("/accounts")
    public ResponseEntity<Map<String, Object>> getDovecotAccounts() {
        log.info("📋 Getting Dovecot accounts list...");
        Map<String, Object> response = new HashMap<>();
        
        try {
            var accounts = mailService.getImapConfig().getAccounts();
            response.put("success", true);
            response.put("accounts", accounts);
            response.put("total", accounts != null ? accounts.size() : 0);
            response.put("timestamp", System.currentTimeMillis());
            log.info("✅ Retrieved {} Dovecot accounts", accounts != null ? accounts.size() : 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error getting accounts: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "UP");
        response.put("service", "Mail Service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");

        // Thêm thông tin mail count vào health check
        try {
            long totalMails = mailService.getTotalMailCount();
            response.put("totalMails", totalMails);
            response.put("mailInfo", "Total mails in database: " + totalMails);
        } catch (Exception e) {
            response.put("mailInfo", "Error getting mail count: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint để kiểm tra mail trong database
     */
    @GetMapping("/test-db")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        log.info("🔍 Testing database connection and mail count...");

        Map<String, Object> response = new HashMap<>();

        try {
            // Đếm tổng số mail trong database
            long totalMails = mailService.getTotalMailCount();
            
            // Lấy 5 mail gần nhất
            List<Mail> recentMails = mailService.getRecentMails(5);

            response.put("success", true);
            response.put("totalMails", totalMails);
            response.put("recentMails", recentMails);
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Database test successful - Total mails: {}", totalMails);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error testing database: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error testing database: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test endpoint đơn giản để kiểm tra mail count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getMailCount() {
        log.info("🔍 Getting mail count from database...");

        Map<String, Object> response = new HashMap<>();

        try {
            long totalMails = mailService.getTotalMailCount();

            response.put("success", true);
            response.put("totalMails", totalMails);
            response.put("message", "Total mails in database: " + totalMails);
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Mail count: {}", totalMails);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting mail count: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test endpoint đơn giản không cần database
     */
    @GetMapping("/simple-test")
    public ResponseEntity<Map<String, Object>> simpleTest() {
        log.info("🔍 Simple test endpoint...");

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Simple test endpoint working!");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint để kiểm tra database connection
     */
    @GetMapping("/test-db-connection")
    public ResponseEntity<Map<String, Object>> testDbConnection() {
        log.info("🔍 Testing database connection...");

        Map<String, Object> response = new HashMap<>();

        try {
            long totalMails = mailService.getTotalMailCountPublic();
            response.put("success", true);
            response.put("message", "Database connection working");
            response.put("totalMails", totalMails);
            response.put("timestamp", System.currentTimeMillis());
            log.info("✅ Database connection test successful - Total mails: {}", totalMails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Database connection test failed: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Kiểm tra mail RECEIVED
     */
    @GetMapping("/check-received")
    public ResponseEntity<Map<String, Object>> checkReceivedMails() {
        log.info("🔍 Checking received mails...");

        Map<String, Object> response = new HashMap<>();

        try {
            long totalMails = mailService.getTotalMailCount();
            List<Mail> receivedMails = mailService.getReceivedMails();
            
            response.put("success", true);
            response.put("totalMails", totalMails);
            response.put("receivedMailsCount", receivedMails.size());
            response.put("receivedMails", receivedMails);
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Found {} received mails out of {} total mails", receivedMails.size(), totalMails);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error checking received mails: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Kiểm tra mail đã tồn tại
     */
    @PostMapping("/check-duplicate")
    public ResponseEntity<Map<String, Object>> checkMailDuplicate(@RequestBody MailDTO request) {
        log.info("🔍 Checking mail duplicate...");

        Map<String, Object> response = new HashMap<>();

        try {
            boolean isDuplicate = mailService.checkMailDuplicate(request);
            
            response.put("success", true);
            response.put("isDuplicate", isDuplicate);
            response.put("message", isDuplicate ? "Mail already exists" : "Mail is new");
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Mail duplicate check completed - isDuplicate: {}", isDuplicate);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error checking mail duplicate: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Lấy thống kê về mail
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMailStats() {
        log.info("📊 Getting mail statistics...");

        Map<String, Object> response = new HashMap<>();

        try {
            long totalMails = mailService.getTotalMailCount();
            List<Mail> sentMails = mailService.getSentMails();
            List<Mail> receivedMails = mailService.getReceivedMails();
            List<Mail> pendingMails = mailService.getPendingMails();
            List<Mail> failedMails = mailService.getFailedMails();
            
            response.put("success", true);
            response.put("totalMails", totalMails);
            response.put("sentMails", sentMails.size());
            response.put("receivedMails", receivedMails.size());
            response.put("pendingMails", pendingMails.size());
            response.put("failedMails", failedMails.size());
            response.put("timestamp", System.currentTimeMillis());

            log.info("✅ Mail statistics - Total: {}, Sent: {}, Received: {}, Pending: {}, Failed: {}", 
                    totalMails, sentMails.size(), receivedMails.size(), pendingMails.size(), failedMails.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting mail statistics: {}", e.getMessage());

            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(response);
        }
    }
}