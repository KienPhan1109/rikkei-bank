package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePinRequest {
    @NotBlank(message = "Mã PIN cũ không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã PIN giao dịch phải chứa đúng 6 chữ số")
    private String oldPin;
    
    @NotBlank(message = "Mã PIN mới không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã PIN giao dịch phải chứa đúng 6 chữ số")
    private String newPin;
}
