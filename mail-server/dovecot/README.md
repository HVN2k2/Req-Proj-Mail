# Dovecot Configuration

## Cấu hình hiện tại

### Files cấu hình:
- `dovecot.conf` - Cấu hình chính
- `10-auth.conf` - Cấu hình xác thực
- `10-mail.conf` - Cấu hình mail
- `auth-passwdfile.conf.ext` - Cấu hình passwd-file driver
- `users` - File chứa user và mật khẩu

### Cải thiện đã thực hiện:

1. **SSL/TLS Security:**
   - Tự động tạo self-signed certificate trong Dockerfile
   - Cấu hình SSL protocols và cipher suites an toàn
   - Bắt buộc sử dụng TLS cho authentication

2. **Authentication Security:**
   - Chuyển từ PLAIN sang SHA512-CRYPT cho mật khẩu
   - Disable plaintext authentication
   - Sử dụng passwd-file driver cho virtual users

3. **Docker Configuration:**
   - Copy đầy đủ các file cấu hình
   - Volume mapping chính xác
   - Expose cả port 143 và 993

## Sử dụng

### Tạo mật khẩu SHA512-CRYPT:
```bash
# Chạy script tạo mật khẩu
./generate_passwords.sh
```

### Build và chạy:
```bash
docker-compose up --build
```

### Test kết nối:
```bash
# Test IMAPS (port 993)
openssl s_client -connect localhost:993 -crlf

# Test IMAP (port 143) - sẽ yêu cầu STARTTLS
telnet localhost 143
```

## Lưu ý bảo mật:

1. **Production Environment:**
   - Thay thế self-signed certificate bằng certificate thật
   - Cấu hình firewall chỉ cho phép port cần thiết
   - Sử dụng strong passwords

2. **Monitoring:**
   - Log files: `/var/log/dovecot.log`
   - Monitor failed login attempts
   - Regular security updates

3. **Backup:**
   - Backup file `/etc/dovecot/passwd`
   - Backup mail data trong `/data/mail/` 