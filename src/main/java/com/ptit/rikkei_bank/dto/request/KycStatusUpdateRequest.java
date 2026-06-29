package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycStatusUpdateRequest {
    @NotBlank(message = "Trạng thái phê duyệt không được để trống")
    private String status; // Hợp lệ: CONFIRM hoặc REJECT
}
