package com.ptit.rikkei_bank.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.ptit.rikkei_bank.validator.UniqueUsername;
import com.ptit.rikkei_bank.validator.UniqueEmail;
import com.ptit.rikkei_bank.validator.UniquePhoneNumber;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class RegisterRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải từ 4 đến 20 ký tự")
    @UniqueUsername(message = "Tên đăng nhập đã tồn tại!")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải chứa ít nhất 6 ký tự")
    @ToString.Exclude
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email không hợp lệ")
    @UniqueEmail(message = "Email đã tồn tại!")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$", message = "Số điện thoại không đúng định dạng của Việt Nam")
    @UniquePhoneNumber(message = "Số điện thoại đã tồn tại!")
    private String phoneNumber;
}
