FROM debian:bullseye

ENV DEBIAN_FRONTEND=noninteractive

# Cài postfix và rsyslog
RUN apt-get update && \
    apt-get install -y postfix rsyslog && \
    mkdir -p /var/spool/postfix && \
    chown postfix:postfix /var/spool/postfix

# Copy file cấu hình postfix
COPY ./main.cf /etc/postfix/main.cf

# Script khởi động: chạy rsyslog + postfix foreground
CMD rsyslogd && postfix start-fg
