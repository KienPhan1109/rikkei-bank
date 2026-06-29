package com.ptit.rikkei_bank.service;

public interface TokenBlacklistService {
    void blacklistToken(String token, long expiryDurationSeconds);
    boolean isBlacklisted(String token);
}
