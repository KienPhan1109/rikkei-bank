package com.ptit.rikkei_bank.mapper;

import com.ptit.rikkei_bank.dto.request.KycSubmitRequest;
import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.entity.KycProfile;
import org.springframework.stereotype.Component;

@Component
public class KycMapper {

    public KycProfile toEntity(KycSubmitRequest request) {
        if (request == null) {
            return null;
        }
        KycProfile profile = new KycProfile();
        profile.setIdNumber(request.getIdNumber());
        profile.setFullName(request.getFullName());
        profile.setDob(request.getDob());
        profile.setSex(request.getSex());
        profile.setAddress(request.getAddress());
        profile.setIdCardFrontUrl(request.getIdCardFrontUrl());
        return profile;
    }

    public KycResponse toResponse(KycProfile profile) {
        if (profile == null) {
            return null;
        }
        KycResponse response = new KycResponse();
        response.setId(profile.getId());
        response.setIdNumber(profile.getIdNumber());
        response.setFullName(profile.getFullName());
        response.setDob(profile.getDob());
        response.setSex(profile.getSex());
        response.setAddress(profile.getAddress());
        response.setIdCardFrontUrl(profile.getIdCardFrontUrl());
        response.setStatus(profile.getStatus() != null ? profile.getStatus().name() : null);
        response.setVerifiedAt(profile.getVerifiedAt());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
        response.setRejectionReason(profile.getRejectionReason());
        return response;
    }
}
