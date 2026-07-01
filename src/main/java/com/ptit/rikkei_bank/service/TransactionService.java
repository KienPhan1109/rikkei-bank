package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.request.TransferRequest;
import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponse transfer(Long userId, String fromAccountNumber, TransferRequest request);
    PageResponse<TransactionResponse> getTransactionHistory(Long userId, String accountNumber, Pageable pageable);
    PageResponse<TransactionResponse> getDepositHistory(Long userId, String accountNumber, Pageable pageable);
    PageResponse<TransactionResponse> getWithdrawalHistory(Long userId, String accountNumber, Pageable pageable);
    PageResponse<TransactionResponse> getAllTransactions(Pageable pageable);
    PageResponse<TransactionResponse> getAllDeposits(Pageable pageable);
    PageResponse<TransactionResponse> getAllWithdrawals(Pageable pageable);
}
