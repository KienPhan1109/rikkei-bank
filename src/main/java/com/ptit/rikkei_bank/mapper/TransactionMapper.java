package com.ptit.rikkei_bank.mapper;

import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionCode(transaction.getTransactionCode());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setStatus(transaction.getStatus());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setFromAccountNumber(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : null);
        response.setToAccountNumber(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : null);
        return response;
    }
}
