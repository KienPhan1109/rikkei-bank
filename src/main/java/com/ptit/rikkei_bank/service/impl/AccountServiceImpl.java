package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.request.AccountCreateRequest;
import com.ptit.rikkei_bank.dto.request.AccountStatusRequest;
import com.ptit.rikkei_bank.dto.request.ChangePinRequest;
import com.ptit.rikkei_bank.dto.request.DepositRequest;
import com.ptit.rikkei_bank.dto.request.WithdrawRequest;
import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.entity.Account;
import com.ptit.rikkei_bank.entity.Transaction;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.BusinessException;
import com.ptit.rikkei_bank.exception.ForbiddenException;
import com.ptit.rikkei_bank.exception.InsufficientBalanceException;
import com.ptit.rikkei_bank.exception.ResourceNotFoundException;
import com.ptit.rikkei_bank.mapper.AccountMapper;
import com.ptit.rikkei_bank.mapper.TransactionMapper;
import com.ptit.rikkei_bank.repository.AccountRepository;
import com.ptit.rikkei_bank.repository.TransactionRepository;
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
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public AccountResponse createAccount(Long userId, AccountCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

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
    public PageResponse<AccountResponse> getAllAccounts(Pageable pageable) {
        Page<AccountResponse> accountsPage = accountRepository.findAllProjected(pageable);
        return PageResponse.of(accountsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber, Long userId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + accountNumber));

        User caller = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        boolean hasAccess = caller.getRole() != null && 
                ("ADMIN".equalsIgnoreCase(caller.getRole().getName()) || "STAFF".equalsIgnoreCase(caller.getRole().getName()));

        if (!account.getUser().getId().equals(userId) && !hasAccess) {
            throw new ForbiddenException("Không có quyền truy cập thông tin tài khoản này!");
        }

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountStatus(String accountNumber, Long userId, AccountStatusRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + accountNumber));

        User caller = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        boolean isAdmin = caller.getRole() != null && "ADMIN".equalsIgnoreCase(caller.getRole().getName());

        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Chỉ chủ tài khoản mới được khóa/mở khóa tài khoản!");
        }

        account.setActive(request.getActive());
        account.setUpdatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public TransactionResponse deposit(Long userId, String accountNumber, DepositRequest request) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + accountNumber));
        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Không có quyền nạp tiền vào tài khoản này!");
        }
        if (account.getActive() == null || !account.getActive()) {
            throw new BusinessException("Tài khoản đang bị khóa!");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        String transactionCode;
        Random random = new Random();
        do {
            transactionCode = "DEP" + System.currentTimeMillis() + (100 + random.nextInt(900));
        } while (transactionRepository.existsByTransactionCode(transactionCode));

        transaction.setTransactionCode(transactionCode);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Nạp tiền vào tài khoản");
        transaction.setStatus("SUCCESS");
        transaction.setFromAccount(null); // Cash deposit
        transaction.setToAccount(account);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long userId, String accountNumber, WithdrawRequest request) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + accountNumber));
        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Không có quyền rút tiền từ tài khoản này!");
        }
        if (account.getActive() == null || !account.getActive()) {
            throw new BusinessException("Tài khoản đang bị khóa!");
        }

        if (!passwordEncoder.matches(request.getTransactionPin(), account.getTransactionPin())) {
            throw new BusinessException("Mã PIN giao dịch không chính xác!");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Số dư tài khoản không đủ để thực hiện giao dịch!");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        String transactionCode;
        Random random = new Random();
        do {
            transactionCode = "WIT" + System.currentTimeMillis() + (100 + random.nextInt(900));
        } while (transactionRepository.existsByTransactionCode(transactionCode));

        transaction.setTransactionCode(transactionCode);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Rút tiền từ tài khoản");
        transaction.setStatus("SUCCESS");
        transaction.setFromAccount(account);
        transaction.setToAccount(null); // Cash withdrawal
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public void changePin(Long userId, String accountNumber, ChangePinRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + accountNumber));
        if (!account.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Không có quyền đổi mã PIN của tài khoản này!");
        }
        
        if (!passwordEncoder.matches(request.getOldPin(), account.getTransactionPin())) {
            throw new BusinessException("Mã PIN cũ không chính xác!");
        }
        
        account.setTransactionPin(passwordEncoder.encode(request.getNewPin()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }
}
