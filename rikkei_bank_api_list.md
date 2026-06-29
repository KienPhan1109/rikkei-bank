# 🔌 DANH SÁCH ENDPOINTS API DỰ ÁN RIKKEI BANK (LARK BASE IMPORT TEMPLATE)

> [!TIP]
> Bạn có thể sao chép bảng dưới đây dán trực tiếp vào tab **API List** trong Lark Base của bạn. Cột **#** tương ứng với các ID API được ánh xạ trong bảng Task.

| # | Method | Endpoints | Description | Role | Category |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | POST | `/api/v1/auth/register` | Đăng ký tài khoản mới. Nhận email, password, username, phoneNumber. Trả về thông tin user đã tạo. | PUBLIC | Authentication |
| 2 | POST | `/api/v1/auth/login` | Đăng nhập hệ thống. Trả về JWT access token + refresh token + thông tin user. | PUBLIC | Authentication |
| 3 | POST | `/api/v1/auth/refresh` | Làm mới access token bằng refresh token. Trả về access token mới. | PUBLIC | Authentication |
| 4 | POST | `/api/v1/auth/logout` | Đăng xuất hệ thống. Vô hiệu hóa token, đưa access token hiện tại vào blacklist. | USER | Authentication |
| 5 | GET | `/api/v1/users` | Lấy danh sách người dùng phân trang bằng JPQL Constructor Projection (Chỉ Admin/Staff). | ADMIN/STAFF | Users |
| 6 | PUT | `/api/v1/users/{id}/status` | Khóa hoặc kích hoạt tài khoản hoạt động của người dùng (Chỉ Admin/Staff). | ADMIN/STAFF | Users |
| 7 | DELETE | `/api/v1/users/{id}` | Xóa vĩnh viễn tài khoản người dùng khỏi hệ thống (Chỉ Admin/Staff). | ADMIN/STAFF | Users |
| 8 | PUT | `/api/v1/users/me/password` | Đổi mật khẩu cá nhân. Nhận oldPassword, newPassword và lưu mật khẩu mã hóa BCrypt. | USER | Users |
| 9 | POST | `/api/v1/kyc/submit` | Khách hàng nộp hồ sơ định danh cá nhân eKYC (Yêu cầu ảnh CCCD và thông tin cá nhân). | USER | eKYC |
| 10 | GET | `/api/v1/kyc/me` | Khách hàng xem lại hồ sơ eKYC cá nhân của mình. | USER | eKYC |
| 11 | PUT | `/api/v1/staff/kyc/{id}/status` | Phê duyệt (CONFIRM) hoặc từ chối (REJECT) hồ sơ eKYC (Chỉ Admin/Staff). | ADMIN/STAFF | eKYC |
| 12 | POST | `/api/v1/accounts` | Mở tài khoản thanh toán mới (Yêu cầu đã eKYC, tự động sinh 10 chữ số ngẫu nhiên, băm PIN). | USER | Accounts |
| 13 | GET | `/api/v1/accounts` | Xem danh sách các tài khoản thanh toán cá nhân đang sở hữu. | USER | Accounts |
| 14 | GET | `/api/v1/accounts/{accountNumber}` | Xem chi tiết thông tin của một tài khoản thanh toán dựa trên số tài khoản. | USER | Accounts |
| 15 | PUT | `/api/v1/accounts/{accountNumber}/status` | Thay đổi trạng thái hoạt động của tài khoản thanh toán (Khóa hoặc Mở khóa). | USER/ADMIN | Accounts |
| 16 | POST | `/api/v1/transactions/transfer` | Thực hiện chuyển tiền liên tài khoản (Kiểm tra số dư, xác thực mã PIN, Lock chống chi tiêu kép). | USER | Transactions |
| 17 | GET | `/api/v1/transactions/history` | Xem lịch sử giao dịch chuyển/nhận tiền của tài khoản thanh toán chỉ định. | USER/ADMIN | Transactions |
| 18 | GET | `/actuator/health` | Kiểm tra trạng thái hoạt động của hệ thống, database và các tài nguyên phụ thuộc. | PUBLIC | Actuator |
| 19 | GET | `/actuator/metrics` | Tra cứu số liệu giám sát hiệu năng hệ thống (CPU, RAM, Request metrics...). | PUBLIC | Actuator |
