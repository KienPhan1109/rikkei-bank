package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.request.AccountCreateRequest;
import com.ptit.rikkei_bank.dto.request.AccountStatusRequest;
import com.ptit.rikkei_bank.dto.request.ChangePinRequest;
import com.ptit.rikkei_bank.dto.request.DepositRequest;
import com.ptit.rikkei_bank.dto.request.WithdrawRequest;
import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountResponse createAccount(Long userId, AccountCreateRequest request);
    PageResponse<AccountResponse> getAccountsByUserId(Long userId, Pageable pageable);
    PageResponse<AccountResponse> getAllAccounts(Pageable pageable);
    AccountResponse getAccountByNumber(String accountNumber, Long userId);
    AccountResponse updateAccountStatus(String accountNumber, Long userId, AccountStatusRequest request);
    
    TransactionResponse deposit(Long userId, String accountNumber, DepositRequest request);
    TransactionResponse withdraw(Long userId, String accountNumber, WithdrawRequest request);
    void changePin(Long userId, String accountNumber, ChangePinRequest request);
}
