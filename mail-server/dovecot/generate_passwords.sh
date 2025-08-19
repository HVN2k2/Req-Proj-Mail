#!/bin/bash

# Script để generate password hash cho Dovecot users
echo "Generating password hashes for Dovecot users..."

# Tạo password hash cho testuser với password "123456"
TESTUSER_HASH=$(openssl passwd -6 -salt "salt123" "123456")
echo "testuser:$TESTUSER_HASH" > /etc/dovecot/passwd

# Tạo password hash cho nhuan2002 với password "123456"  
NHUAN_HASH=$(openssl passwd -6 -salt "salt456" "123456")
echo "nhuan2002:$NHUAN_HASH" >> /etc/dovecot/passwd

echo "Password file created at /etc/dovecot/passwd"
echo "Users:"
echo "  testuser:123456"
echo "  nhuan2002:123456" 