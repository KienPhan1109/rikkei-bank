# 🔌 DANH SÁCH ENDPOINTS API DỰ ÁN RIKKEI BANK (LARK BASE IMPORT TEMPLATE)

> [!TIP]
> Bạn có thể sao chép bảng dưới đây dán trực tiếp vào tab **API List** trong Lark Base của bạn. Cột **#** tương ứng với các ID API được ánh xạ trong bảng Task.

| # | Method | Endpoints | Description | Role | Category |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | POST | `/api/v1/auth/register` | Khởi tạo tài khoản người dùng mới. Yêu cầu cung cấp thông tin định danh (email, mật khẩu, tên đăng nhập, số điện thoại). Phản hồi mã trạng thái 201 Created. | PUBLIC | Authentication |
| 2 | POST | `/api/v1/auth/login` | Xác thực thông tin người dùng và khởi tạo phiên làm việc. Phản hồi chuỗi Access Token (JWT) và Refresh Token. | PUBLIC | Authentication |
| 3 | POST | `/api/v1/auth/refresh` | Tái cấp phát Access Token thông qua Refresh Token hợp lệ để duy trì phiên làm việc liên tục. | PUBLIC | Authentication |
| 4 | POST | `/api/v1/auth/logout` | Chấm dứt phiên làm việc hiện tại, vô hiệu hóa quyền truy cập bằng cách đưa Access Token vào danh sách từ chối (Blacklist). | USER | Authentication |
| 5 | GET | `/api/v1/users` | Truy xuất danh sách tài khoản người dùng. Ứng dụng kỹ thuật Constructor Projection trong JPQL để phân trang dữ liệu tối ưu. | ADMIN/STAFF | Users |
| 6 | PUT | `/api/v1/users/{id}/status` | Cập nhật trạng thái hoạt động của tài khoản người dùng (Kích hoạt hoặc Khóa tài khoản tạm thời). | ADMIN/STAFF | Users |
| 7 | DELETE | `/api/v1/users/{id}` | Thực thi cơ chế xóa mềm (Soft Delete) đối với tài khoản người dùng nhằm bảo toàn tính toàn vẹn dữ liệu hệ thống. | ADMIN/STAFF | Users |
| 8 | PUT | `/api/v1/users/me/password` | Cập nhật mật khẩu cá nhân. Yêu cầu cung cấp mật khẩu hiện tại và áp dụng thuật toán băm BCrypt cho mật khẩu mới. | USER | Users |
| 9 | POST | `/api/v1/kyc/submit` | Tiếp nhận và lưu trữ hồ sơ định danh cá nhân (eKYC), bao gồm dữ liệu hình ảnh CCCD và các thuộc tính nhân thân. | USER | eKYC |
| 10 | GET | `/api/v1/kyc/me` | Trích xuất và phản hồi thông tin hồ sơ định danh (eKYC) hiện tại của người dùng đang thực hiện truy vấn. | USER | eKYC |
| 11 | PUT | `/api/v1/staff/kyc/{id}/status` | Đánh giá và cập nhật trạng thái phê duyệt (CONFIRM) hoặc từ chối (REJECT) đối với hồ sơ eKYC của khách hàng. | ADMIN/STAFF | eKYC |
| 12 | POST | `/api/v1/accounts` | Khởi tạo tài khoản thanh toán tự động cho người dùng đã hoàn tất eKYC. Thuật toán sinh tự động 10 chữ số định danh tài khoản. | USER | Accounts |
| 13 | GET | `/api/v1/accounts` | Liệt kê danh sách các tài khoản thanh toán đang hoạt động thuộc quyền sở hữu của người dùng dưới định dạng phân trang. | USER | Accounts |
| 14 | GET | `/api/v1/accounts/{accountNumber}` | Trích xuất thông tin chi tiết về số dư và trạng thái của một tài khoản thanh toán cụ thể. | USER | Accounts |
| 15 | PUT | `/api/v1/accounts/{accountNumber}/status` | Can thiệp thay đổi trạng thái hoạt động của tài khoản thanh toán (Khóa hoặc Mở khóa) dựa trên chính sách rủi ro. | USER/ADMIN | Accounts |
| 16 | POST | `/api/v1/transactions/transfer` | Thực thi giao dịch chuyển khoản nội bộ. Áp dụng Khóa bi quan (Pessimistic Locking) và đảm bảo tính chất nguyên tử (ACID). | USER | Transactions |
| 17 | GET | `/api/v1/transactions/history` | Truy vấn và phản hồi lịch sử giao dịch tài chính dưới định dạng cấu trúc dữ liệu phân trang (PageResponse). | USER/ADMIN | Transactions |
| 18 | GET | `/actuator/health` | Kiểm định trạng thái khả dụng của ứng dụng, cơ sở dữ liệu và các thành phần tích hợp hệ thống. | PUBLIC | Actuator |
| 19 | GET | `/actuator/metrics` | Truy xuất chỉ số giám sát hiệu năng hệ thống theo thời gian thực (Mức độ tiêu thụ CPU, Bộ nhớ, HTTP Request Metrics). | PUBLIC | Actuator |
