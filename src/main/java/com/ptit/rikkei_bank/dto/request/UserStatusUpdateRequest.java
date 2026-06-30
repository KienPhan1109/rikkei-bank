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
public class UserStatusUpdateRequest {
    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean active;
}
