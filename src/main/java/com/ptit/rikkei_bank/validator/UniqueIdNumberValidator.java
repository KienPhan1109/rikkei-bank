package com.ptit.rikkei_bank.validator;

import com.ptit.rikkei_bank.repository.KycProfileRepository;
import com.ptit.rikkei_bank.security.CustomUserDetails;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UniqueIdNumberValidator implements ConstraintValidator<UniqueIdNumber, String> {

    private final KycProfileRepository kycProfileRepository;

    @Override
    public boolean isValid(String idNumber, ConstraintValidatorContext context) {
        if (idNumber == null || idNumber.trim().isEmpty()) {
            return true; // Let @NotBlank handle this
        }

        // Nếu người dùng đã nộp hồ sơ eKYC trước đó, ta cho qua validator này 
        // để tầng Service ném ra lỗi "Người dùng đã nộp hồ sơ định danh eKYC!" trước các lỗi khác.
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                Long userId = userDetails.getUser().getId();
                if (kycProfileRepository.existsByUserId(userId)) {
                    return true; 
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất thông tin người dùng từ SecurityContext trong quá trình validate CCCD: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xác thực thông tin người dùng hiện tại", e);
        }

        return !kycProfileRepository.existsByIdNumber(idNumber);
    }
}
