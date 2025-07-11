FROM debian:bullseye

ENV DEBIAN_FRONTEND=noninteractive

# Cài Dovecot
RUN apt-get update && apt-get install -y dovecot-imapd rsyslog

# Tạo file log để tail
RUN touch /var/log/dovecot.log && chmod 644 /var/log/dovecot.log

# Copy file cấu hình dovecot
COPY dovecot.conf /etc/dovecot/dovecot.conf

# Start rsyslog + dovecot foreground và tail log
CMD sh -c "rsyslogd && dovecot && tail -f /var/log/dovecot.log"
