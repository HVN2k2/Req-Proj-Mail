FROM debian:bullseye

RUN apt update && apt install -y dovecot-core dovecot-imapd && apt clean

COPY dovecot.conf /etc/dovecot/dovecot.conf
COPY 10-auth.conf /etc/dovecot/conf.d/10-auth.conf
COPY 10-mail.conf /etc/dovecot/conf.d/10-mail.conf

CMD ["dovecot", "-F"]
