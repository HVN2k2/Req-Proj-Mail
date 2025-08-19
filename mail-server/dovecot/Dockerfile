FROM debian:bullseye

ENV DEBIAN_FRONTEND=noninteractive

# Cài Dovecot
RUN apt-get update && apt-get install -y dovecot-imapd rsyslog openssl

# Tạo thư mục SSL và tạo self-signed certificate
RUN mkdir -p /etc/ssl/certs /etc/ssl/private && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/ssl/private/mailserver.key \
    -out /etc/ssl/certs/mailserver.crt \
    -subj "/C=VN/ST=Hanoi/L=Hanoi/O=MailServer/CN=mail.hvn.com"

# Tạo file log để tail
RUN touch /var/log/dovecot.log && chmod 644 /var/log/dovecot.log

# Cấu hình rsyslog đơn giản để tránh lỗi kernel log
RUN echo "*.info;mail.none;authpriv.none;cron.none /var/log/messages" > /etc/rsyslog.conf && \
    echo "authpriv.* /var/log/secure" >> /etc/rsyslog.conf && \
    echo "mail.* /var/log/maillog" >> /etc/rsyslog.conf && \
    echo "*.emerg /var/log/messages" >> /etc/rsyslog.conf

# Copy cấu hình đơn giản
COPY dovecot-simple.conf /etc/dovecot/dovecot.conf

# Tạo users với password hash đúng cho development
RUN echo "testuser:\$6\$salt123\$uTMCjGvQpbnZvjM4Ehm8mp96uis3x7ogShsX7DC647VP4Eu5AnalHMrmSsjKdVlgn71YFB.0nV0IPWmXJkp9f1" > /etc/dovecot/passwd && \
    echo "nhuan2002:\$6\$salt456\$uTMCjGvQpbnZvjM4Ehm8mp96uis3x7ogShsX7DC647VP4Eu5AnalHMrmSsjKdVlgn71YFB.0nV0IPWmXJkp9f1" >> /etc/dovecot/passwd && \
    echo "honhuan2002:\$6\$salt789\$uTMCjGvQpbnZvjM4Ehm8mp96uis3x7ogShsX7DC647VP4Eu5AnalHMrmSsjKdVlgn71YFB.0nV0IPWmXJkp9f1" >> /etc/dovecot/passwd

# Start rsyslog + dovecot foreground và tail log
CMD sh -c "rsyslogd -n & dovecot -F"
