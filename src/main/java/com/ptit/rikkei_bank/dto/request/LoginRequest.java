package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @ToString.Exclude
    private String password;
}
