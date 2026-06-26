# Báo cáo Tổng quan Dự án Rikkei Bank (Rikkei Bank Report)

## 1. Thông tin chung
- **Tên dự án:** Hệ thống Quản lý Ngân hàng Rikkei Bank.
- **Loại ứng dụng:** Java Web Service cung cấp RESTful API (Stateless Backend).
- **Công nghệ chính:** Spring Boot, Spring Data JPA, Spring Security (JWT), MySQL, Lombok.
- **Nguyên tắc code:** Tuân thủ chặt chẽ theo `coding_rules.md` (Dùng Mapper thủ công, các tầng Service/Repository chia thành Interface + Impl).

## 2. Tiến độ Hiện tại
- **Giai đoạn 1 (Nền tảng & CSDL):** Đã hoàn tất khởi tạo cấu trúc, Base classes (`ApiResponse`, `GlobalExceptionHandler`), Entities và Repositories.
- **Giai đoạn 2 (Bảo mật & Xác thực):** Đã hoàn tất tích hợp JWT, Security Filters, Custom User Details, `AuthService`, `AuthController` (Đăng ký, Đăng nhập, Làm mới Token, Đăng xuất) và exception handler.
- **Giai đoạn 3 (Core Banking):** Chưa bắt đầu.

## 3. Cấu trúc Database (ERD)
- Xem chi tiết sơ đồ tại: [erd.md](file:///d:/Phan%20Trung%20Ki%C3%AAn%20-%20PTIT/JAVA%20WEB%20SERVICE/Rikkei%20Bank/erd.md) (Tương thích dbdiagram.io)
- **Danh sách Bảng (Tables):**
  - `roles`: Lưu quyền truy cập.
  - `users`: Quản lý người dùng.
  - `kyc_profiles`: Hồ sơ định danh eKYC (quan hệ 1-1 với users).
  - `accounts`: Tài khoản ngân hàng (quan hệ 1-N với users).
  - `transactions`: Giao dịch luân chuyển dòng tiền (quan hệ N-1 với accounts).
  - `refresh_tokens`: Lưu trữ Refresh Token cho phiên đăng nhập.
  - `token_blacklists`: Lưu danh sách Access Token bị chặn (sau khi đăng xuất).

## 4. Danh sách API (Postman Collection)
- Xem chi tiết tại: [Rikkei_Bank.postman_collection.json](file:///d:/Phan%20Trung%20Ki%C3%AAn%20-%20PTIT/JAVA%20WEB%20SERVICE/Rikkei%20Bank/Rikkei_Bank.postman_collection.json)
- **Auth APIs (`/api/v1/auth`)**:
  - `POST /register`: Đăng ký tài khoản mới (Validates: rỗng, email sai định dạng).
  - `POST /login`: Đăng nhập, nhận Access Token & Refresh Token (Validates: sai pass, trống dữ liệu).
  - `POST /refresh`: Cấp lại JWT bằng Refresh Token (Validates: sai token, hết hạn).
  - `POST /logout`: Đăng xuất, vô hiệu hóa Access Token (Validates: không có token).
- **User APIs (`/api/v1/users`)**:
  - `GET /`: Lấy toàn bộ danh sách người dùng (Hiện tại đang cấu hình public để dễ test).

## 5. Quy trình làm việc (Workflow Bắt buộc)
Bất kỳ sự thay đổi hay bổ sung nào liên quan đến Database, API, hay tính năng mới **bắt buộc** phải được cập nhật đồng bộ ở 3 file sau:
1. **`report.md`**: Cập nhật tổng quan dự án, tiến độ và danh sách API.
2. **`erd.md`**: Bổ sung hoặc chỉnh sửa schema các bảng và quan hệ.
3. **`Rikkei_Bank.postman_collection.json`**: Cập nhật collection, bắt buộc test nhiều trường hợp (Success, 404, Bad Request) và không dùng icon.
