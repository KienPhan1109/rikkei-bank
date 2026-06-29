package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.dto.request.KycSubmitRequest;
import com.ptit.rikkei_bank.dto.request.KycStatusUpdateRequest;
import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.entity.KycProfile;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.enums.Status;
import com.ptit.rikkei_bank.exception.BusinessException;
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
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng!"));

        if (kycProfileRepository.existsByUserId(userId)) {
            throw new BusinessException("Người dùng đã nộp hồ sơ định danh eKYC!");
        }

        KycProfile profile = kycMapper.toEntity(request);
        profile.setStatus(Status.PENDING);
        profile.setUser(user);
        profile.setCreatedAt(LocalDateTime.now());

        KycProfile savedProfile = kycProfileRepository.save(profile);
        return kycMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional
    public KycResponse updateKycStatus(Long kycId, KycStatusUpdateRequest request) {
        KycProfile profile = kycProfileRepository.findById(kycId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ định danh!"));

        Status status;
        try {
            status = Status.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Trạng thái phê duyệt không hợp lệ! Chỉ chấp nhận CONFIRM hoặc REJECT.");
        }

        profile.setStatus(status);
        profile.setVerifiedAt(LocalDateTime.now());

        if (status == Status.CONFIRM) {
            User user = profile.getUser();
            user.setIsKyc(true);
            userRepository.save(user);
        } else if (status == Status.REJECT) {
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
                .orElseThrow(() -> new BusinessException("Người dùng chưa nộp hồ sơ định danh eKYC!"));
        return kycMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycById(Long kycId) {
        KycProfile profile = kycProfileRepository.findById(kycId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ định danh!"));
        return kycMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KycResponse> getAllKycProfiles(Pageable pageable) {
        Page<KycResponse> kycPage = kycProfileRepository.findAllProjected(pageable);
        return PageResponse.of(kycPage);
    }
}
