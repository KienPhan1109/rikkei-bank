package com.ptit.rikkei_bank.validator;

import com.ptit.rikkei_bank.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // Let @NotBlank handle this
        }
        return !userRepository.existsByPhoneNumber(phoneNumber);
    }
}
