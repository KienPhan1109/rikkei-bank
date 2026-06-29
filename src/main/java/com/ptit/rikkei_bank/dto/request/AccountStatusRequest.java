package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatusRequest {
    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean active;
}
