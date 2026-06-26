package com.ptit.rikkei_bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean isKyc;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
