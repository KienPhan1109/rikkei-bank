package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.request.TransferRequest;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.entity.Account;
import com.ptit.rikkei_bank.entity.Transaction;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.exception.BusinessException;
import com.ptit.rikkei_bank.mapper.TransactionMapper;
import com.ptit.rikkei_bank.repository.AccountRepository;
import com.ptit.rikkei_bank.repository.TransactionRepository;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.service.TransactionService;
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
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TransactionResponse transfer(Long userId, TransferRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Số tiền chuyển khoản phải lớn hơn 0!");
        }

        Account fromAccount = accountRepository.findByAccountNumberForUpdate(request.getFromAccountNumber())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản nguồn!"));

        Account toAccount = accountRepository.findByAccountNumberForUpdate(request.getToAccountNumber())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản đích!"));

        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new BusinessException("Không thể thực hiện chuyển khoản đến cùng một tài khoản!");
        }

        if (fromAccount.getActive() == null || !fromAccount.getActive()) {
            throw new BusinessException("Tài khoản nguồn đang bị khóa!");
        }

        if (toAccount.getActive() == null || !toAccount.getActive()) {
            throw new BusinessException("Tài khoản đích đang bị khóa!");
        }

        if (!fromAccount.getUser().getId().equals(userId)) {
            throw new BusinessException("Không có quyền thực hiện giao dịch từ tài khoản này!");
        }

        if (!passwordEncoder.matches(request.getTransactionPin(), fromAccount.getTransactionPin())) {
            throw new BusinessException("Mã PIN giao dịch không chính xác!");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Số dư tài khoản không đủ để thực hiện giao dịch!");
        }

        // Deduct from sender and add to receiver
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        fromAccount.setUpdatedAt(LocalDateTime.now());
        toAccount.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Generate unique transaction code
        String transactionCode;
        Random random = new Random();
        do {
            transactionCode = "TX" + System.currentTimeMillis() + (100 + random.nextInt(900));
        } while (transactionRepository.existsByTransactionCode(transactionCode));

        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus("SUCCESS");
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedTransaction);
    }
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getTransactionHistory(Long userId, String accountNumber, Pageable pageable) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản ngân hàng!"));

        User caller = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));

        boolean isAdmin = caller.getRole() != null && "ADMIN".equalsIgnoreCase(caller.getRole().getName());

        if (!account.getUser().getId().equals(userId) && !isAdmin) {
            throw new BusinessException("Không có quyền truy cập lịch sử giao dịch của tài khoản này!");
        }

        Page<TransactionResponse> txPage = transactionRepository.findByAccountProjected(account.getId(), pageable);
        return PageResponse.of(txPage);
    }
}
