package com.hvn_proj_mail.mail.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "mail.imap")
public class ImapConfig {
    private String host = "localhost";
    private int port = 143;
    private boolean ssl = false;
    
    // Cấu hình mặc định cho tài khoản chính
    private String defaultUsername;
    private String defaultPassword;
    
    // Danh sách các tài khoản Dovecot
    private List<DovecotAccount> accounts;
    
    // Map để truy cập nhanh tài khoản theo email
    private Map<String, DovecotAccount> accountMap;

    @Data
    public static class DovecotAccount {
        private String email;
        private String username;
        private String password;
        private boolean enabled = true;
        private String description;
    }

    // Thêm getter/setter để đảm bảo
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }

    public String getDefaultUsername() { return defaultUsername; }
    public void setDefaultUsername(String defaultUsername) { this.defaultUsername = defaultUsername; }

    public String getDefaultPassword() { return defaultPassword; }
    public void setDefaultPassword(String defaultPassword) { this.defaultPassword = defaultPassword; }

    public List<DovecotAccount> getAccounts() { return accounts; }
    public void setAccounts(List<DovecotAccount> accounts) { this.accounts = accounts; }

    // Lấy tài khoản theo email
    public DovecotAccount getAccountByEmail(String email) {
        if (accounts == null) return null;
        return accounts.stream()
                .filter(acc -> acc.getEmail().equals(email) && acc.isEnabled())
                .findFirst()
                .orElse(null);
    }

    // Lấy tài khoản mặc định
    public DovecotAccount getDefaultAccount() {
        if (accounts != null && !accounts.isEmpty()) {
            return accounts.stream()
                    .filter(DovecotAccount::isEnabled)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // Kiểm tra xem có tài khoản nào không
    public boolean hasAccounts() {
        return accounts != null && !accounts.isEmpty();
    }
}