package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.request.KycSubmitRequest;
import com.ptit.rikkei_bank.dto.request.KycStatusUpdateRequest;
import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.entity.KycProfile;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.enums.Status;
import com.ptit.rikkei_bank.exception.BusinessException;
import com.ptit.rikkei_bank.exception.ResourceNotFoundException;
import com.ptit.rikkei_bank.mapper.KycMapper;
import com.ptit.rikkei_bank.repository.KycProfileRepository;
import com.ptit.rikkei_bank.repository.UserRepository;
import com.ptit.rikkei_bank.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final KycProfileRepository kycProfileRepository;
    private final UserRepository userRepository;
    private final KycMapper kycMapper;

    @Override
    @Transactional
    public KycResponse submitKyc(Long userId, KycSubmitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        KycProfile existingProfile = kycProfileRepository.findByUserId(userId).orElse(null);
        KycProfile profile;
        if (existingProfile != null) {
            if (existingProfile.getStatus() == Status.REJECT) {
                // Check if the new ID number is already taken by another user
                if (!existingProfile.getIdNumber().equals(request.getIdNumber()) &&
                        kycProfileRepository.existsByIdNumber(request.getIdNumber())) {
                    throw new BusinessException("Số CMND/CCCD đã tồn tại trên hệ thống!");
                }
                // Update in-place to avoid Hibernate insert-before-delete constraint violations
                profile = existingProfile;
                profile.setIdNumber(request.getIdNumber());
                profile.setFullName(request.getFullName());
                profile.setDob(request.getDob());
                profile.setSex(request.getSex());
                profile.setAddress(request.getAddress());
                profile.setIdCardFrontUrl(request.getIdCardFrontUrl());
                profile.setStatus(Status.PENDING);
                profile.setRejectionReason(null); // Clear previous rejection reason
                profile.setCreatedAt(LocalDateTime.now());
                profile.setVerifiedAt(null);
            } else {
                throw new BusinessException("Người dùng đã nộp hồ sơ định danh eKYC!");
            }
        } else {
            if (kycProfileRepository.existsByIdNumber(request.getIdNumber())) {
                throw new BusinessException("Số CMND/CCCD đã tồn tại trên hệ thống!");
            }
            profile = kycMapper.toEntity(request);
            profile.setStatus(Status.PENDING);
            profile.setUser(user);
            profile.setCreatedAt(LocalDateTime.now());
        }

        KycProfile savedProfile = kycProfileRepository.save(profile);
        return kycMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional
    public KycResponse updateKycStatus(Long kycId, KycStatusUpdateRequest request) {
        KycProfile profile = kycProfileRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ định danh với ID: " + kycId));

        Status status;
        try {
            status = Status.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Trạng thái phê duyệt không hợp lệ! Chỉ chấp nhận CONFIRM hoặc REJECT.");
        }

        profile.setStatus(status);
        profile.setVerifiedAt(LocalDateTime.now());

        if (status == Status.CONFIRM) {
            profile.setRejectionReason(null);
            User user = profile.getUser();
            user.setIsKyc(true);
            userRepository.save(user);
        } else if (status == Status.REJECT) {
            if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
                throw new BusinessException("Vui lòng cung cấp lý do từ chối duyệt eKYC!");
            }
            profile.setRejectionReason(request.getRejectionReason().trim());
            User user = profile.getUser();
            user.setIsKyc(false);
            userRepository.save(user);
        }

        KycProfile savedProfile = kycProfileRepository.save(profile);
        return kycMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycByUserId(Long userId) {
        KycProfile profile = kycProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng chưa nộp hồ sơ định danh eKYC!"));
        return kycMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycById(Long kycId) {
        KycProfile profile = kycProfileRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ định danh với ID: " + kycId));
        return kycMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KycResponse> getAllKycProfiles(Pageable pageable) {
        Page<KycResponse> kycPage = kycProfileRepository.findAllProjected(pageable);
        return PageResponse.of(kycPage);
    }
}
