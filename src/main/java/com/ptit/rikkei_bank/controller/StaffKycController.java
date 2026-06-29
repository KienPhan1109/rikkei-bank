package com.ptit.rikkei_bank.controller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.ptit.rikkei_bank.dto.request.KycStatusUpdateRequest;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff/kyc")
@RequiredArgsConstructor
public class StaffKycController {

    private final KycService kycService;

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<KycResponse>> updateKycStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody KycStatusUpdateRequest request) {
        KycResponse response = kycService.updateKycStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Cập nhật trạng thái duyệt hồ sơ định danh thành công!",
                response
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KycResponse>> getKycById(
            @PathVariable("id") Long id) {
        KycResponse response = kycService.getKycById(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy thông tin hồ sơ định danh thành công",
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<KycResponse>>> getAllKycProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<KycResponse> responses = kycService.getAllKycProfiles(pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Lấy danh sách hồ sơ eKYC thành công",
                responses
        ));
    }
}
