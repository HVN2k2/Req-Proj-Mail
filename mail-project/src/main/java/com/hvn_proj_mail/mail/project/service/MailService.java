package com.hvn_proj_mail.mail.project.service;

import com.hvn_proj_mail.mail.project.config.ImapConfig;
import com.hvn_proj_mail.mail.project.entity.Mail;
import com.hvn_proj_mail.mail.project.entity.User;
import com.hvn_proj_mail.mail.project.exception.MailAlreadyExistsException;
import com.hvn_proj_mail.mail.project.repository.MailRepository;
import com.hvn_proj_mail.mail.project.repository.UserRepository;
import com.hvn_proj_mail.mail.project.request.MailDTO;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MailService {

    MailRepository mailRepository;
    UserRepository userRepository;
    JavaMailSender mailSender;
    ImapConfig imapConfig;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Helper method để lấy current user ID (Long)
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    // Helper method để lấy current user email
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Khởi động monitoring mail real-time
     */
    public void startMailMonitoring() {
        log.info("🚀 Starting mail monitoring service...");

        // Chạy ngay lập tức
        scheduler.schedule(this::fetchNewMails, 0, TimeUnit.SECONDS);

        // Chạy định kỳ mỗi 30 giây
        scheduler.scheduleAtFixedRate(this::fetchNewMails, 30, 30, TimeUnit.SECONDS);

        log.info("✅ Mail monitoring started - checking every 30 seconds");
    }

    /**
     * Lấy mail mới từ IMAP server cho user đang đăng nhập
     */
    public void fetchNewMails() {
        // Lấy thông tin user đang đăng nhập
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            log.warn("⚠️ No user logged in, cannot fetch mails");
            return;
        }

        log.info("📧 Fetching new mails for logged-in user: {}", currentUserEmail);

        // Tìm tài khoản Dovecot tương ứng với user đang đăng nhập
        ImapConfig.DovecotAccount userAccount = null;
        
        if (imapConfig.hasAccounts()) {
            // Tìm trong danh sách tài khoản cấu hình
            userAccount = imapConfig.getAccounts().stream()
                    .filter(acc -> acc.getEmail().equals(currentUserEmail) && acc.isEnabled())
                    .findFirst()
                    .orElse(null);
        }

        if (userAccount != null) {
            // Sử dụng tài khoản từ cấu hình
            log.info("🔄 Found configured account for user: {}", currentUserEmail);
            try {
                fetchNewMailsForAccount(userAccount.getUsername(), userAccount.getPassword());
            } catch (Exception e) {
                log.error("❌ Error fetching mails for user {}: {}", currentUserEmail, e.getMessage());
            }
        } else {
            // Fallback: sử dụng tài khoản mặc định
            log.warn("⚠️ No configured account found for user: {}, using default account", currentUserEmail);
            fetchNewMailsForAccount(imapConfig.getDefaultUsername(), imapConfig.getDefaultPassword());
        }
    }

    /**
     * Lấy mail mới từ IMAP server cho một tài khoản cụ thể
     */
    public void fetchNewMailsForAccount(String username, String password) {
        fetchNewMailsForAccount(username, password, null);
    }

    /**
     * Lấy mail mới từ IMAP server cho một tài khoản cụ thể với user email
     */
    public void fetchNewMailsForAccount(String username, String password, String userEmail) {
        try {
            log.info("📧 Fetching new mails for account: {}", username);

            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.host", imapConfig.getHost());
            props.setProperty("mail.imap.port", String.valueOf(imapConfig.getPort()));

            if (imapConfig.isSsl()) {
                props.setProperty("mail.imap.ssl.enable", "true");
                props.setProperty("mail.imap.ssl.trust", "*");
            } else {
                // Sử dụng STARTTLS cho non-SSL connection
                props.setProperty("mail.imap.starttls.enable", "true");
                props.setProperty("mail.imap.ssl.trust", "*");
            }

            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imap");

            // Kết nối đến IMAP server
            store.connect(imapConfig.getHost(), imapConfig.getPort(), username, password);
            log.info("✅ Connected to IMAP server for account: {}", username);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Lấy các mail mới (chưa đọc)
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            log.info("📬 Found {} new messages for account: {}", messages.length, username);

            int processedCount = 0;
            int skippedCount = 0;
            int errorCount = 0;

            for (Message message : messages) {
                try {
                    // Sử dụng userEmail nếu có, nếu không thì dùng username
                    String targetUser = userEmail != null ? userEmail : username;
                    boolean wasProcessed = processMessageWithResult(message, username);
                    if (wasProcessed) {
                        processedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    log.error("❌ Error processing message for account {}: {}", username, e.getMessage());
                    errorCount++;
                }
            }

            log.info("📊 Mail processing summary for {} - Processed: {}, Skipped: {}, Errors: {}", 
                    username, processedCount, skippedCount, errorCount);

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            log.error("❌ Error fetching mails for account {}: {}", username, e.getMessage());
        }
    }

    /**
     * Lấy TẤT CẢ mail từ IMAP server (bao gồm cả đã đọc)
     */
    public int fetchAllMails() {
        try {
            log.info("📧 Fetching ALL mails from IMAP server...");

            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.host", imapConfig.getHost());
            props.setProperty("mail.imap.port", String.valueOf(imapConfig.getPort()));

            if (imapConfig.isSsl()) {
                props.setProperty("mail.imap.ssl.enable", "true");
                props.setProperty("mail.imap.ssl.trust", "*");
            } else {
                // Sử dụng STARTTLS cho non-SSL connection
                props.setProperty("mail.imap.starttls.enable", "true");
                props.setProperty("mail.imap.ssl.trust", "*");
            }

            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imap");

            // Kết nối đến IMAP server với tài khoản mặc định
            store.connect(imapConfig.getHost(), imapConfig.getPort(), imapConfig.getDefaultUsername(), imapConfig.getDefaultPassword());
            log.info("✅ Connected to IMAP server: {}:{}", imapConfig.getHost(), imapConfig.getPort());

            // Thử fetch từ INBOX trước
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();
            
            // Nếu không có mail trong INBOX, thử fetch từ thư mục NEW
            if (messages.length == 0) {
                log.info("📬 No messages in INBOX, trying NEW folder...");
                try {
                    Folder newFolder = store.getFolder("NEW");
                    newFolder.open(Folder.READ_ONLY);
                    messages = newFolder.getMessages();
                    log.info("📬 Found {} messages in NEW folder", messages.length);
                } catch (Exception e) {
                    log.warn("⚠️ Could not access NEW folder: {}", e.getMessage());
                }
            }

            log.info("📬 Found {} total messages", messages.length);
            
            // Debug: Log thông tin từng message
            for (int i = 0; i < messages.length; i++) {
                try {
                    Message msg = messages[i];
                    String subject = msg.getSubject();
                    String from = getAddresses(msg.getFrom());
                    String to = getAddresses(msg.getRecipients(Message.RecipientType.TO));
                    log.info("📧 Message {}: From={}, To={}, Subject={}", i+1, from, to, subject);
                } catch (Exception e) {
                    log.error("❌ Error getting message {} info: {}", i+1, e.getMessage());
                }
            }

            int processedCount = 0;
            for (Message message : messages) {
                try {
                    // Force process message without duplicate check
                    processMessageForce(message);
                    processedCount++;
                } catch (Exception e) {
                    log.error("❌ Error processing message: {}", e.getMessage());
                }
            }

            inbox.close(false);
            store.close();

            return processedCount;

        } catch (Exception e) {
            log.error("❌ Error fetching all mails: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Kiểm tra mail đã tồn tại trong database
     */
    private boolean isMailExists(String sender, String receiver, String subject, String content, String userId) {
        try {
            // Kiểm tra dựa trên sender, receiver, subject và message content
            boolean existsByContent = mailRepository.existsBySenderReceiverSubjectAndMessage(
                sender, receiver, subject, content, userId);
            
            if (existsByContent) {
                log.info("📧 Mail already exists (by content) - From: {}, To: {}, Subject: {}", sender, receiver, subject);
                return true;
            }
            
            // Kiểm tra dựa trên thời gian (trong vòng 60 giây)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.minusSeconds(30); // 30 giây trước
            LocalDateTime endTime = now.plusSeconds(30);    // 30 giây sau
            
            boolean existsByTime = mailRepository.existsBySenderReceiverSubjectAndTime(
                sender, receiver, subject, userId, startTime, endTime);
            
            if (existsByTime) {
                log.info("📧 Mail already exists (by time) - From: {}, To: {}, Subject: {}", sender, receiver, subject);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("❌ Error checking mail existence: {}", e.getMessage());
            return false; // Nếu có lỗi, cho phép lưu để tránh mất mail
        }
    }

    /**
     * Xử lý từng message và lưu vào database
     */
    private void processMessage(Message message) throws MessagingException, IOException {
        processMessageWithResult(message);
    }

    /**
     * Xử lý từng message và lưu vào database (trả về kết quả)
     */
    private boolean processMessageWithResult(Message message) throws MessagingException, IOException {
        return processMessageWithResult(message, null);
    }

    /**
     * Xử lý từng message và lưu vào database (trả về kết quả) với username cụ thể
     */
    private boolean processMessageWithResult(Message message, String username) throws MessagingException, IOException {
        try {
            // Lấy thông tin cơ bản
            String subject = message.getSubject();
            String from = getAddresses(message.getFrom());
            String to = getAddresses(message.getRecipients(Message.RecipientType.TO));

            // Lấy nội dung
            String content = getMessageContent(message);

            // Tìm user dựa trên email receiver hoặc username
            User user = null;
            if (username != null) {
                // Tìm user theo username từ Dovecot
                user = findUserByUsername(username);
                if (user == null) {
                    log.warn("⚠️ User not found for username: {}", username);
                    return false;
                }
            } else {
                // Fallback: tìm user theo email receiver
                user = findUserByEmail(to);
                if (user == null) {
                    log.warn("⚠️ User not found for email: {}", to);
                    return false;
                }
            }

            // Kiểm tra mail đã tồn tại
            if (isMailExists(from, to, subject != null ? subject : "", content, String.valueOf(user.getId()))) {
                log.info("📧 Skipping duplicate mail - From: {}, To: {}, Subject: {}", from, to, subject);
                return false; // Bỏ qua mail đã tồn tại
            }

            // Tạo mail entity
            Mail mail = Mail.builder()
                    .userId(String.valueOf(user.getId()))
                    .receiver(to)
                    .sender(from)
                    .subject(subject != null ? subject : "")
                    .message(content)
                    .status(Mail.MailStatus.RECEIVED)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Lưu vào database
            Mail savedMail = mailRepository.save(mail);
            log.info("✅ Saved mail to database - ID: {}, Subject: {}", savedMail.getId(), subject);
            return true; // Xử lý thành công

        } catch (Exception e) {
            log.error("❌ Error processing message: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Xử lý từng message và lưu vào database (FORCE - không check duplicate)
     */
    private void processMessageForce(Message message) throws MessagingException, IOException {
        try {
            // Lấy thông tin cơ bản
            String subject = message.getSubject();
            String from = getAddresses(message.getFrom());
            String to = getAddresses(message.getRecipients(Message.RecipientType.TO));

            // Lấy nội dung
            String content = getMessageContent(message);

            // Debug log
            log.info("🔍 Processing message - From: {}, To: {}, Subject: {}", from, to, subject);

            // Tìm user dựa trên email receiver
            User user = findUserByEmail(to);
            if (user == null) {
                log.warn("⚠️ User not found for email: {}", to);
                return;
            }

            log.info("✅ Found user: {} (ID: {})", user.getEmail(), user.getId());

            // Tạo mail entity với timestamp hiện tại để tránh duplicate
            Mail mail = Mail.builder()
                    .userId(String.valueOf(user.getId()))
                    .receiver(to)
                    .sender(from)
                    .subject(subject != null ? subject : "")
                    .message(content)
                    .status(Mail.MailStatus.RECEIVED)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Debug log để kiểm tra status
            log.info("🔍 Mail status before save: {}", mail.getStatus());

            // Lưu vào database (force save)
            Mail savedMail = mailRepository.save(mail);
            log.info("✅ FORCE SAVED mail to database - ID: {}, Subject: {}, Status: {}", savedMail.getId(), subject, savedMail.getStatus());

        } catch (Exception e) {
            log.error("❌ Error processing message: {}", e.getMessage());
        }
    }

    /**
     * Lấy nội dung message
     */
    private String getMessageContent(Message message) throws MessagingException, IOException {
        Object content = message.getContent();

        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            StringBuilder contentBuilder = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.getContent() instanceof String) {
                    contentBuilder.append((String) bodyPart.getContent());
                }
            }

            return contentBuilder.toString();
        }

        return "";
    }

    /**
     * Lấy địa chỉ email từ Address array
     */
    private String getAddresses(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Address address : addresses) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(address.toString());
        }

        return sb.toString();
    }

    /**
     * Tìm user theo email
     */
    private User findUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }

        // Tách email đầu tiên nếu có nhiều email
        String firstEmail = email.split(",")[0].trim();

        return userRepository.findByEmail(firstEmail).orElse(null);
    }

    /**
     * Tìm user theo username (Dovecot username)
     */
    private User findUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        // Tìm user có email chứa username
        // Ví dụ: username = "testuser" -> tìm email "testuser@hvn.com"
        String emailPattern = username + "@hvn.com";
        return userRepository.findByEmail(emailPattern).orElse(null);
    }

    public void sendMail(MailDTO request) {
        Long currentUserId = getCurrentUserId();
        String currentUserEmail = getCurrentUserEmail();

        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Kiểm tra mail đã tồn tại trước khi gửi
        if (isMailExists(currentUserEmail, request.getTo(), request.getSubject(), request.getMessage(), String.valueOf(currentUserId))) {
            throw new MailAlreadyExistsException("Mail with same content already exists for this user");
        }

        Mail entity = Mail.builder()
                .userId(String.valueOf(currentUserId)) // Sử dụng Long
                .sender(currentUserEmail) // Lấy từ current user
                .receiver(request.getTo())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(Mail.MailStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        mailRepository.save(entity);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getTo());
            message.setFrom(currentUserEmail); // Lấy từ current user
            message.setSubject(request.getSubject());
            message.setText(request.getMessage());
            mailSender.send(message);
            entity.setStatus(Mail.MailStatus.SENT);
        }
        catch (Exception e) {
            log.error("Lỗi gửi mail: {}", e.getMessage());
            entity.setStatus(Mail.MailStatus.FAILED);
            mailRepository.save(entity);
            throw new RuntimeException("Gửi mail thất bại: " + e.getMessage(), e);
        }
        mailRepository.save(entity);
    }

    public Page<Mail> getInbox(String receiver, int page, int size) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        Pageable pageable = PageRequest.of(page, size);
        return mailRepository.findByUserIdAndReceiverOrderByCreatedAtDesc(String.valueOf(currentUserId), receiver, pageable);
    }

    public List<Mail> getPendingMails(){
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return mailRepository.findByUserIdAndStatus(String.valueOf(currentUserId), Mail.MailStatus.PENDING);
    }

    public List<Mail> getSentMails(){
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return mailRepository.findByUserIdAndStatus(String.valueOf(currentUserId), Mail.MailStatus.SENT);
    }

    public List<Mail> getReceivedMails(){
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return mailRepository.findByUserIdAndStatus(String.valueOf(currentUserId), Mail.MailStatus.RECEIVED);
    }

    public List<Mail> getFailedMails(){
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return mailRepository.findByUserIdAndStatus(String.valueOf(currentUserId), Mail.MailStatus.FAILED);
    }

    public void deleteMail(Long id) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        // Chỉ xóa mail của current user
        Mail mail = mailRepository.findById(id).orElse(null);
        if (mail != null && mail.getUserId().equals(currentUserId)) {
            mailRepository.deleteById(id);
        }
    }

    /**
     * Test kết nối IMAP
     */
    public boolean testImapConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.host", imapConfig.getHost());
            props.setProperty("mail.imap.port", String.valueOf(imapConfig.getPort()));

            // Debug: Log cấu hình
            log.info("🔧 IMAP Config - Host: {}, Port: {}, Default Username: {}, SSL: {}", 
                    imapConfig.getHost(), imapConfig.getPort(), imapConfig.getDefaultUsername(), imapConfig.isSsl());

            if (imapConfig.isSsl()) {
                // Kết nối IMAP SSL (cổng 993)
                props.setProperty("mail.imap.ssl.enable", "true");
                props.setProperty("mail.imap.ssl.trust", "*");
            } else {
                // Kết nối IMAP STARTTLS (cổng 143)
                props.setProperty("mail.imap.starttls.enable", "true");
                props.setProperty("mail.imap.ssl.trust", "*");
            }

            // Thêm debug properties
            props.setProperty("mail.debug", "true");
            props.setProperty("mail.imap.debug", "true");

            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imap");

            log.info("🔌 Connecting to IMAP server {}:{} with username: {} ...", 
                    imapConfig.getHost(), imapConfig.getPort(), imapConfig.getDefaultUsername());
            
            store.connect(imapConfig.getHost(), imapConfig.getPort(), imapConfig.getDefaultUsername(), imapConfig.getDefaultPassword());
            log.info("✅ IMAP connection test successful");

            store.close();
            return true;

        } catch (AuthenticationFailedException e) {
            log.error("❌ Authentication failed: {}", e.getMessage());
            log.error("❌ Username: {}, Password: {}", imapConfig.getDefaultUsername(), "***");
            return false;
        } catch (MessagingException e) {
            log.error("❌ Messaging error: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Unknown error: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Dừng monitoring service
     */
    public void stopMailMonitoring() {
        log.info("🛑 Stopping mail monitoring service...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        log.info("✅ Mail monitoring service stopped");
    }

    public void fetchInbox(String host, String username, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", "143");
            props.put("mail.imap.starttls.enable", "true");

            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Subject: " + message.getSubject());
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Đếm tổng số mail trong database
     */
    public long getTotalMailCount() {
        return mailRepository.count();
    }

    /**
     * Đếm tổng số mail trong database (public method - không cần authentication)
     */
    public long getTotalMailCountPublic() {
        return mailRepository.count();
    }

    /**
     * Lấy danh sách mail gần nhất
     */
    public List<Mail> getRecentMails(int limit) {
        return mailRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    /**
     * Kiểm tra mail đã tồn tại (public method cho controller)
     */
    public boolean checkMailDuplicate(MailDTO request) {
        Long currentUserId = getCurrentUserId();
        String currentUserEmail = getCurrentUserEmail();

        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        return isMailExists(currentUserEmail, request.getTo(), request.getSubject(), request.getMessage(), String.valueOf(currentUserId));
    }

    /**
     * Lấy cấu hình IMAP (public method cho controller)
     */
    public ImapConfig getImapConfig() {
        return imapConfig;
    }

    /**
     * Lấy mail theo ID
     */
    public Mail getMailById(Long id) {
        return mailRepository.findById(id).orElse(null);
    }

    /**
     * Lấy mail theo ID (public method - không cần authentication)
     */
    public Mail getMailByIdPublic(Long id) {
        return mailRepository.findById(id).orElse(null);
    }

    /**
     * Lấy mail theo email receiver (public method - không cần authentication)
     */
    public List<Mail> getMailsByEmailPublic(String email) {
        return mailRepository.findByReceiverOrderByCreatedAtDesc(email);
    }

    /**
     * Fetch mail cho user cụ thể (cho admin hoặc khi cần)
     */
    public void fetchMailsForUser(String userEmail) {
        log.info("📧 Fetching mails for specific user: {}", userEmail);
        
        // Tìm tài khoản Dovecot tương ứng
        ImapConfig.DovecotAccount userAccount = null;
        
        if (imapConfig.hasAccounts()) {
            userAccount = imapConfig.getAccounts().stream()
                    .filter(acc -> acc.getEmail().equals(userEmail) && acc.isEnabled())
                    .findFirst()
                    .orElse(null);
        }

        if (userAccount != null) {
            log.info("🔄 Found configured account for user: {}", userEmail);
            try {
                fetchNewMailsForAccount(userAccount.getUsername(), userAccount.getPassword(), userEmail);
            } catch (Exception e) {
                log.error("❌ Error fetching mails for user {}: {}", userEmail, e.getMessage());
            }
        } else {
            log.error("❌ No configured account found for user: {}", userEmail);
        }
    }

}