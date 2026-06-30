package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawRequest {
    @NotNull(message = "Số tiền rút không được để trống")
    @DecimalMin(value = "2000", message = "Số tiền rút tối thiểu là 2000 VND")
    private BigDecimal amount;
    
    private String description;
    
    @NotBlank(message = "Mã PIN giao dịch không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã PIN giao dịch phải chứa đúng 6 chữ số")
    private String transactionPin;
}
