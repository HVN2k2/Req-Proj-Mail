package com.hvn_proj_mail.mail.project.exception;

/**
 * Exception được throw khi mail đã tồn tại trong database
 */
public class MailAlreadyExistsException extends RuntimeException {
    
    public MailAlreadyExistsException(String message) {
        super(message);
    }
    
    public MailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
