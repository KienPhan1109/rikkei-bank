package com.ptit.rikkei_bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklists")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenBlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String accessToken;

    @Column(nullable = false)
    private LocalDateTime expiryAt;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
