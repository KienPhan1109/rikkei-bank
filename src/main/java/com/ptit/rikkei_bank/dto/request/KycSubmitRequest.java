package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.ptit.rikkei_bank.validator.UniqueIdNumber;
import com.ptit.rikkei_bank.validator.ValidDob;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycSubmitRequest {

    @NotBlank(message = "Số CCCD/CMND không được để trống")
    @Pattern(regexp = "^(\\d{9}|\\d{12})$", message = "Số CCCD/CMND phải bao gồm 9 hoặc 12 chữ số")
    @UniqueIdNumber(message = "Số CCCD/CMND đã tồn tại!")
    private String idNumber;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @ValidDob(minAge = 14)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    @NotBlank(message = "Giới tính không được để trống")
    private String sex;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Ảnh mặt trước CCCD không được để trống")
    private MultipartFile idCardFrontUrl;
}
