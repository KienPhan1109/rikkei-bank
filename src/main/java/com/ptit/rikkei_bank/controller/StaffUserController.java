package com.ptit.rikkei_bank.controller;

import com.ptit.rikkei_bank.dto.request.UserStatusUpdateRequest;
import com.ptit.rikkei_bank.dto.response.ApiResponse;
import com.ptit.rikkei_bank.dto.response.PageResponse;
import com.ptit.rikkei_bank.dto.response.UserResponse;
import com.ptit.rikkei_bank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/users")
@RequiredArgsConstructor
public class StaffUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> usersPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Lấy danh sách người dùng thành công", 
                PageResponse.of(usersPage)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Lấy thông tin người dùng thành công", 
                user
        ));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        userService.toggleUserStatus(id, request.getActive());
        String message = request.getActive() ? "Kích hoạt người dùng thành công!" : "Khóa người dùng thành công!";
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                message, 
                null
        ));
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable("id") Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Khóa người dùng thành công!", 
                null
        ));
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable("id") Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), 
                "Mở khóa người dùng thành công!", 
                null
        ));
    }
}
