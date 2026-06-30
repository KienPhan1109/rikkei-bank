package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequest {
    @NotNull(message = "Số tiền nạp không được để trống")
    @DecimalMin(value = "2000", message = "Số tiền nạp tối thiểu là 2000 VND")
    private BigDecimal amount;
    
    private String description;
}
