# 🔌 DANH SÁCH ENDPOINTS API DỰ ÁN RIKKEI BANK (LARK BASE IMPORT TEMPLATE)

> [!TIP]
> Bạn có thể sao chép bảng dưới đây dán trực tiếp vào tab **API List** trong Lark Base của bạn. Cột **#** tương ứng với các ID API được ánh xạ trong bảng Task.

| # | Method | Endpoints | Description | Role | Category |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | POST | `/api/v1/auth/register` | Khởi tạo tài khoản người dùng mới. Yêu cầu cung cấp thông tin định danh (email, mật khẩu, tên đăng nhập, số điện thoại). Phản hồi mã trạng thái 201 Created. | PUBLIC | Authentication |
| 2 | POST | `/api/v1/auth/login` | Xác thực thông tin người dùng và khởi tạo phiên làm việc. Phản hồi chuỗi Access Token (JWT) và Refresh Token. | PUBLIC | Authentication |
| 3 | POST | `/api/v1/auth/refresh` | Tái cấp phát Access Token thông qua Refresh Token hợp lệ để duy trì phiên làm việc liên tục. | PUBLIC | Authentication |
| 4 | POST | `/api/v1/auth/logout` | Chấm dứt phiên làm việc hiện tại, vô hiệu hóa quyền truy cập bằng cách đưa Access Token vào danh sách từ chối (Blacklist). | USER | Authentication |
| 5 | GET | `/api/v1/staff/users` | Truy xuất danh sách tài khoản người dùng. Ứng dụng kỹ thuật Constructor Projection trong JPQL để phân trang dữ liệu tối ưu. | ADMIN/STAFF | Users |
| 6 | GET | `/api/v1/staff/users/{id}` | Lấy thông tin chi tiết một người dùng cụ thể bằng ID. | ADMIN/STAFF | Users |
| 8 | POST | `/api/v1/staff/users/{id}/lock` | Khóa tài khoản người dùng và khóa dây chuyền toàn bộ tài khoản thanh toán của họ. | ADMIN/STAFF | Users |
| 9 | POST | `/api/v1/staff/users/{id}/unlock` | Mở khóa tài khoản người dùng và tự động kích hoạt lại toàn bộ tài khoản thanh toán của họ. | ADMIN/STAFF | Users |
| 10 | PUT | `/api/v1/users/me/password` | Cập nhật mật khẩu cá nhân. Yêu cầu cung cấp mật khẩu hiện tại và áp dụng thuật toán băm BCrypt cho mật khẩu mới. | USER | Users |
| 11 | POST | `/api/v1/kyc` | Tiếp nhận và lưu trữ hồ sơ định danh cá nhân (eKYC), bao gồm dữ liệu hình ảnh CCCD và các thuộc tính nhân thân. Hỗ trợ cập nhật đè khi bị REJECT. | USER | eKYC |
| 12 | GET | `/api/v1/kyc` | Trích xuất và phản hồi thông tin hồ sơ định danh (eKYC) hiện tại của người dùng đang thực hiện truy vấn. | USER | eKYC |
| 13 | GET | `/api/v1/staff/kyc` | Lấy danh sách tất cả các hồ sơ định danh eKYC dạng phân trang trong hệ thống. | ADMIN/STAFF | eKYC |
| 14 | GET | `/api/v1/staff/kyc/{id}` | Lấy thông tin chi tiết của một hồ sơ eKYC theo ID. | ADMIN/STAFF | eKYC |
| 15 | PUT | `/api/v1/staff/kyc/{id}/status` | Đánh giá và cập nhật trạng thái phê duyệt (CONFIRM) hoặc từ chối (REJECT - yêu cầu kèm theo lý do cụ thể). | ADMIN/STAFF | eKYC |
| 16 | POST | `/api/v1/accounts` | Khởi tạo tài khoản thanh toán tự động cho người dùng đã hoàn tất eKYC. Thuật toán sinh tự động 10 chữ số định danh tài khoản. | USER | Accounts |
| 17 | GET | `/api/v1/accounts` | Liệt kê danh sách các tài khoản thanh toán đang hoạt động thuộc quyền sở hữu của người dùng dưới định dạng phân trang. | USER | Accounts |
| 18 | GET | `/api/v1/accounts/{accountNumber}` | Trích xuất thông tin chi tiết về số dư và trạng thái của một tài khoản thanh toán cụ thể. | USER | Accounts |
| 19 | PUT | `/api/v1/accounts/{accountNumber}/status` | Can thiệp thay đổi trạng thái hoạt động của tài khoản thanh toán (Khóa hoặc Mở khóa) dựa trên chính sách rủi ro. | USER | Accounts |
| 20 | POST | `/api/v1/accounts/{accountNumber}/deposits` | Nạp tiền mặt vào tài khoản thanh toán cụ thể (RESTful). | USER | Accounts |
| 21 | POST | `/api/v1/accounts/{accountNumber}/withdrawals` | Rút tiền mặt từ tài khoản thanh toán cụ thể sử dụng mã PIN xác thực (RESTful). | USER | Accounts |
| 22 | PUT | `/api/v1/accounts/{accountNumber}/pin` | Thay đổi mã PIN giao dịch bảo mật của tài khoản. | USER | Accounts |
| 23 | GET | `/api/v1/staff/accounts` | Lấy danh sách tất cả tài khoản thanh toán trên toàn hệ thống dạng phân trang. | ADMIN/STAFF | Accounts |
| 24 | POST | `/api/v1/accounts/{accountNumber}/transfers` | Thực thi giao dịch chuyển khoản nội bộ từ tài khoản nguồn. Áp dụng Khóa bi quan (Pessimistic Locking) và đảm bảo tính chất nguyên tử (ACID). | USER | Transactions |
| 25 | GET | `/api/v1/accounts/{accountNumber}/transactions` | Truy vấn lịch sử giao dịch tài chính của một tài khoản cụ thể dưới định dạng phân trang (RESTful). | USER/ADMIN/STAFF | Transactions |
| 26 | GET | `/api/v1/staff/transactions` | Xem toàn bộ danh sách giao dịch tài chính trên toàn hệ thống dạng phân trang. | ADMIN/STAFF | Transactions |
| 27 | GET | `/api/v1/actuator/health` | Kiểm định trạng thái khả dụng của ứng dụng, cơ sở dữ liệu và các thành phần tích hợp hệ thống. | PUBLIC | Actuator |
| 28 | GET | `/api/v1/actuator/metrics` | Truy xuất chỉ số giám sát hiệu năng hệ thống theo thời gian thực (Mức độ tiêu thụ CPU, Bộ nhớ, HTTP Request Metrics). | PUBLIC | Actuator |
| 29 | GET | `/api/v1/accounts/{accountNumber}/deposits` | Lấy lịch sử giao dịch nạp tiền mặt của tài khoản cụ thể dưới định dạng phân trang. | USER | Accounts |
| 30 | GET | `/api/v1/accounts/{accountNumber}/withdrawals` | Lấy lịch sử giao dịch rút tiền mặt của tài khoản cụ thể dưới định dạng phân trang. | USER | Accounts |
| 31 | GET | `/api/v1/staff/transactions/deposits` | Lấy toàn bộ danh sách giao dịch nạp tiền trên hệ thống dạng phân trang (Dành cho ADMIN/STAFF). | ADMIN/STAFF | Transactions |
| 32 | GET | `/api/v1/staff/transactions/withdrawals` | Lấy toàn bộ danh sách giao dịch rút tiền trên hệ thống dạng phân trang (Dành cho ADMIN/STAFF). | ADMIN/STAFF | Transactions |
| 33 | GET | `/api/v1/staff/accounts/{accountNumber}/deposits` | Lấy lịch sử giao dịch nạp tiền mặt của tài khoản cụ thể dưới định dạng phân trang (Dành cho ADMIN/STAFF). | ADMIN/STAFF | Accounts |
| 34 | GET | `/api/v1/staff/accounts/{accountNumber}/withdrawals` | Lấy lịch sử giao dịch rút tiền mặt của tài khoản cụ thể dưới định dạng phân trang (Dành cho ADMIN/STAFF). | ADMIN/STAFF | Accounts |
