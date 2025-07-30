FROM debian:bullseye

ENV DEBIAN_FRONTEND=noninteractive

# Cài postfix, rsyslog và mailutils
RUN apt-get update && \
    apt-get install -y postfix rsyslog mailutils && \
    mkdir -p /var/spool/postfix && \
    chown postfix:postfix /var/spool/postfix

# Copy cấu hình postfix
COPY ./main.cf /etc/postfix/main.cf
COPY vmailbox /etc/postfix/vmailbox
# Tạo file log nếu chưa có
RUN touch /var/log/mail.log && chmod 644 /var/log/mail.log

# Start script: rsyslog + postfix foreground
CMD sh -c "rsyslogd && postfix start && tail -f /var/log/mail.log"
RUN postmap /etc/postfix/vmailbox

