package com.ptit.rikkei_bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionCode;
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private String fromAccountNumber;
    private String toAccountNumber;
}
