package com.ptit.rikkei_bank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class JwtProperties {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("#{${app.jwt.expiration}}")
    private Long expiration;

    @Value("#{${app.jwt.refreshExpiration}}")
    private Long refreshExpiration;
}
