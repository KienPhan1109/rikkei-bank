package com.ptit.rikkei_bank.controller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.ptit.rikkei_bank.dto.request.TransferRequest;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import com.ptit.rikkei_bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TransferRequest request) {
        Long userId = userDetails.getUser().getId();
        TransactionResponse response = transactionService.transfer(userId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Thực hiện giao dịch chuyển khoản thành công!",
                response
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getTransactionHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = userDetails.getUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<TransactionResponse> responses = transactionService.getTransactionHistory(userId, accountNumber, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy lịch sử giao dịch thành công",
                responses
        ));
    }
}
