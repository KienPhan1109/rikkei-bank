package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    @NotBlank(message = "Số tài khoản nguồn không được để trống")
    private String fromAccountNumber;

    @NotBlank(message = "Số tài khoản đích không được để trống")
    private String toAccountNumber;

    @NotNull(message = "Số tiền chuyển khoản không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền chuyển khoản phải lớn hơn 0")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "Mã PIN giao dịch không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã PIN giao dịch phải chứa đúng 6 chữ số")
    private String transactionPin;
}
