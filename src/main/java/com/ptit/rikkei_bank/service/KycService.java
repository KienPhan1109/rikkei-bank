package com.ptit.rikkei_bank.service;

import com.ptit.rikkei_bank.dto.request.KycSubmitRequest;
import com.ptit.rikkei_bank.dto.request.KycStatusUpdateRequest;
import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface KycService {
    KycResponse submitKyc(Long userId, KycSubmitRequest request);
    KycResponse updateKycStatus(Long kycId, KycStatusUpdateRequest request);
    KycResponse getKycByUserId(Long userId);
    KycResponse getKycById(Long kycId);
    PageResponse<KycResponse> getAllKycProfiles(Pageable pageable);
}
