package com.ptit.rikkei_bank.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class DobValidator implements ConstraintValidator<ValidDob, LocalDate> {

    private int minAge;

    @Override
    public void initialize(ValidDob constraintAnnotation) {
        this.minAge = constraintAnnotation.minAge();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) {
            return true; // Use @NotNull for null checks
        }

        LocalDate now = LocalDate.now();
        if (dob.isAfter(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày sinh không được ở trong tương lai")
                   .addConstraintViolation();
            return false;
        }

        int age = Period.between(dob, now).getYears();
        if (age < minAge) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Bạn phải đủ " + minAge + " tuổi để sử dụng dịch vụ")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
