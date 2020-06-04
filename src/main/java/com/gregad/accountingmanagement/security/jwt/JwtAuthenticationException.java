package com.gregad.accountingmanagement.security.jwt;

import org.springframework.security.core.AuthenticationException;

import javax.security.auth.message.AuthException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
