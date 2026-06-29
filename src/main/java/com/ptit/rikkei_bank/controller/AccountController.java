package com.ptit.rikkei_bank.controller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.ptit.rikkei_bank.dto.request.AccountCreateRequest;
import com.ptit.rikkei_bank.dto.request.AccountStatusRequest;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import com.ptit.rikkei_bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AccountCreateRequest request) {
        Long userId = userDetails.getUser().getId();
        AccountResponse response = accountService.createAccount(userId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Mở tài khoản thanh toán thành công!",
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> getMyAccounts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = userDetails.getUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AccountResponse> responses = accountService.getAccountsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy danh sách tài khoản thành công",
                responses
        ));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("accountNumber") String accountNumber) {
        Long userId = userDetails.getUser().getId();
        AccountResponse response = accountService.getAccountByNumber(accountNumber, userId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy thông tin tài khoản thành công",
                response
        ));
    }

    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("accountNumber") String accountNumber,
            @Valid @RequestBody AccountStatusRequest request) {
        Long userId = userDetails.getUser().getId();
        AccountResponse response = accountService.updateAccountStatus(accountNumber, userId, request);
        String msg = request.getActive() ? "Mở khóa tài khoản thành công!" : "Khóa tài khoản thành công!";
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                msg,
                response
        ));
    }
}
