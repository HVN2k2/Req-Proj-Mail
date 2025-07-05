FROM debian:bullseye

RUN apt update && apt install -y postfix && apt clean

COPY main.cf /etc/postfix/main.cf

CMD ["postfix", "start-fg"]
