package com.ptit.rikkei_bank.controller;

import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.service.AccountService;
import com.ptit.rikkei_bank.service.TransactionService;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/accounts")
@RequiredArgsConstructor
public class StaffAccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AccountResponse> responses = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy danh sách toàn bộ tài khoản thành công",
                responses
        ));
    }

    @GetMapping("/{accountNumber}/deposits")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getAccountDeposits(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = userDetails.getUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<TransactionResponse> responses = transactionService.getDepositHistory(userId, accountNumber, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff lấy lịch sử nạp tiền tài khoản thành công",
                responses
        ));
    }

    @GetMapping("/{accountNumber}/withdrawals")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getAccountWithdrawals(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = userDetails.getUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<TransactionResponse> responses = transactionService.getWithdrawalHistory(userId, accountNumber, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff lấy lịch sử rút tiền tài khoản thành công",
                responses
        ));
    }
}
