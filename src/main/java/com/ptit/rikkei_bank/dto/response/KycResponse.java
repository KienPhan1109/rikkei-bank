package com.ptit.rikkei_bank.dto.response;
import com.ptit.rikkei_bank.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycResponse {
    private Long id;
    private String idNumber;
    private String fullName;
    private LocalDate dob;
    private String sex;
    private String address;
    private String idCardFrontUrl;
    private String status;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private Long userId;
    private String rejectionReason;

    public KycResponse(Long id, String idNumber, String fullName, LocalDate dob, String sex, String address, String idCardFrontUrl, Status status, LocalDateTime verifiedAt, LocalDateTime createdAt, Long userId, String rejectionReason) {
        this.id = id;
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.dob = dob;
        this.sex = sex;
        this.address = address;
        this.idCardFrontUrl = idCardFrontUrl;
        this.status = status != null ? status.name() : null;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
        this.userId = userId;
        this.rejectionReason = rejectionReason;
    }
}
