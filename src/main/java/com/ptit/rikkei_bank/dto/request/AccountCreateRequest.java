package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateRequest {

    private String currency = "VND";

    @NotBlank(message = "Mã PIN giao dịch không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã PIN giao dịch phải chứa đúng 6 chữ số")
    private String transactionPin;
}
