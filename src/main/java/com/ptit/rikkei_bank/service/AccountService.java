package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.request.AccountCreateRequest;
import com.ptit.rikkei_bank.dto.request.AccountStatusRequest;
import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(Long userId, AccountCreateRequest request);
    PageResponse<AccountResponse> getAccountsByUserId(Long userId, Pageable pageable);
    AccountResponse getAccountByNumber(String accountNumber, Long userId);
    AccountResponse updateAccountStatus(String accountNumber, Long userId, AccountStatusRequest request);
}
