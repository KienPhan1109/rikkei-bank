package com.ptit.rikkei_bank.controller;

import com.ptit.rikkei_bank.dto.request.KycSubmitRequest;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import com.ptit.rikkei_bank.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping
    public ResponseEntity<ApiResponse<KycResponse>> submitKyc(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute KycSubmitRequest request) {
        Long userId = userDetails.getUser().getId();
        KycResponse response = kycService.submitKyc(userId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Nộp hồ sơ định danh eKYC thành công. Vui lòng chờ Admin phê duyệt!",
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<KycResponse>> getMyKyc(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        KycResponse response = kycService.getKycByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy hồ sơ định danh eKYC thành công",
                response
        ));
    }
}
