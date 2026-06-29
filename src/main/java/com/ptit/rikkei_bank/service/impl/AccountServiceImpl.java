package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.request.AccountCreateRequest;
import com.ptit.rikkei_bank.dto.request.AccountStatusRequest;
import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.entity.Account;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.BusinessException;
import com.ptit.rikkei_bank.mapper.AccountMapper;
import com.ptit.rikkei_bank.repository.AccountRepository;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AccountResponse createAccount(Long userId, AccountCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));

        if (user.getIsKyc() == null || !user.getIsKyc()) {
            throw new BusinessException("Tài khoản chưa thực hiện định danh eKYC! Vui lòng thực hiện KYC trước khi mở tài khoản.");
        }

        String currency = request.getCurrency() != null && !request.getCurrency().trim().isEmpty() 
                ? request.getCurrency().trim().toUpperCase() 
                : "VND";

        String encodedPin = passwordEncoder.encode(request.getTransactionPin());

        // Generate unique 10-digit account number
        String accountNumber;
        Random random = new Random();
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            accountNumber = sb.toString();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(currency);
        account.setTransactionPin(encodedPin);
        account.setActive(true);
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> getAccountsByUserId(Long userId, Pageable pageable) {
        Page<AccountResponse> accountsPage = accountRepository.findByUserIdProjected(userId, pageable);
        return PageResponse.of(accountsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber, Long userId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản ngân hàng!"));

        User caller = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));

        boolean isAdmin = caller.getRole() != null && "ADMIN".equalsIgnoreCase(caller.getRole().getName());

        if (!account.getUser().getId().equals(userId) && !isAdmin) {
            throw new BusinessException("Không có quyền truy cập thông tin tài khoản này!");
        }

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountStatus(String accountNumber, Long userId, AccountStatusRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản ngân hàng!"));

        User caller = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));

        boolean isAdmin = caller.getRole() != null && "ADMIN".equalsIgnoreCase(caller.getRole().getName());

        if (!account.getUser().getId().equals(userId) && !isAdmin) {
            throw new BusinessException("Không có quyền thay đổi trạng thái tài khoản này!");
        }

        account.setActive(request.getActive());
        account.setUpdatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }
}
