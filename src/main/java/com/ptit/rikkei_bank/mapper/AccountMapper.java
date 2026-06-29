package com.ptit.rikkei_bank.mapper;

import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setActive(account.getActive());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        response.setUserId(account.getUser() != null ? account.getUser().getId() : null);
        return response;
    }
}
