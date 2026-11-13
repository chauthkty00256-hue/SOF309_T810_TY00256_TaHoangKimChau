# ĐẶC TẢ NGHIỆP VỤ — CHATBOX THESTARS

Tài liệu mô tả hành vi xác nhận từ mã nguồn hiện tại, làm ngữ cảnh thiết kế kiểm thử. Những quy tắc chưa được triển khai không được coi là tiêu chí đã đáp ứng.

## 1. Thông tin dự án

| Thuộc tính | Mô tả |
|---|---|
| Tên ứng dụng | ChatBox TheStars — ứng dụng nhắn tin nội bộ theo nhóm |
| Backend | Java 17, Spring Boot 3.3.5, Spring MVC, Spring Security, JDBC |
| Giao diện | JSP, HTML/CSS/JavaScript |
| Cơ sở dữ liệu | Microsoft SQL Server, database `ChatBox_TheStars` |
| Giao tiếp thời gian thực | WebSocket/STOMP, SockJS |
| Đóng gói | Maven WAR |
| URL local | `http://localhost:8080/login`; nếu chạy Tomcat context path `chatbox`: `http://localhost:8080/chatbox/login` |
| Trang chính | `/` — giao diện chat sau khi đăng nhập |

## 2. Vai trò và dữ liệu

- **Khách:** xem trang đăng nhập/đăng ký; không được gọi API cần xác thực.
- **Người dùng:** có tài khoản trong `Users`; có thể nhắn tin, tạo nhóm, quản lý bạn bè và cập nhật hồ sơ của mình.
- **OWNER:** người tạo nhóm; quản lý vai trò, chuyển quyền chủ nhóm và xóa thành viên.
- **DEPUTY:** do OWNER bổ nhiệm; được xóa thành viên thường, không được xóa OWNER hoặc DEPUTY khác.
- **MEMBER:** thành viên thông thường của nhóm.
- Dữ liệu cốt lõi: `Users`, `Conversations`, `Participants`, `Messages`, `Attachments`, quan hệ bạn bè và thông báo.

## 3. Quy tắc nghiệp vụ

### 3.1 Tài khoản và xác thực

1. Form đăng ký nhận họ tên hiển thị, email, nhóm/phòng ban, mật khẩu và xác nhận mật khẩu. Giao diện cung cấp nhóm `IT`, `Marketing`, `Design`; kiểm tra xác nhận mật khẩu thực hiện ở trình duyệt.
2. API đăng ký yêu cầu `email`, `displayName`, `team`, `password`; họ tên tối đa 70 ký tự; email đã tồn tại bị từ chối. Mật khẩu mới lưu bằng BCrypt; vai trò mặc định `USER`, trạng thái ban đầu `OFFLINE`.
3. API hiện chưa đặt quy tắc độ dài/độ phức tạp mật khẩu, chưa giới hạn email theo miền Gmail và chưa kiểm tra danh sách team ở backend. Không coi các quy tắc này là yêu cầu đã được bảo đảm.
4. Đăng nhập bằng email/mật khẩu. Sai thông tin hoặc thiếu mật khẩu trả `401`; thành công tạo Bearer token và đặt trạng thái `ONLINE`.
5. Mật khẩu legacy dạng văn bản thường được đổi sang BCrypt sau lần đăng nhập đúng. Token lưu trong bộ nhớ ứng dụng nên restart làm mất phiên đăng nhập.
6. Đăng xuất nhận Bearer token, thu hồi token và đặt người dùng tương ứng thành `OFFLINE`.
7. Người dùng đã xác thực có thể xem hồ sơ, đổi tên hiển thị (không rỗng, tối đa 70 ký tự), tải avatar có MIME type bắt đầu bằng `image/`.

### 3.2 Cuộc trò chuyện và nhóm

1. Người dùng xem danh sách cuộc trò chuyện mình tham gia. Chat đơn giữa cùng một cặp người dùng được tạo hoặc mở lại.
2. Tạo nhóm với tên tối đa 70 ký tự. Người tạo là `OWNER`, thành viên được thêm ban đầu là `MEMBER`.
3. Người dùng có thể xem danh sách nhóm chưa tham gia và tham gia nhóm. Người không tham gia bị từ chối khi xem chi tiết hoặc lịch sử tin nhắn.
4. `OWNER` bổ nhiệm/hạ cấp `DEPUTY`, chuyển quyền sở hữu và xóa thành viên. `DEPUTY` chỉ xóa được `MEMBER` thường.
5. Khi OWNER rời nhóm, quyền sở hữu chuyển cho người được chọn/được hệ thống chọn; nếu OWNER là thành viên cuối cùng, nhóm bị giải tán.
6. Có lịch sử tin nhắn phân trang, đánh dấu đã đọc và hiện diện `ONLINE`, `AWAY`, `OFFLINE`.

### 3.3 Tin nhắn và thông báo

1. STOMP client gửi tin nhắn đến `/app/chat.send/{conversationId}`; server phát tới `/topic/conversation/{conversationId}`. Tin nhắn có thể trả lời tin nhắn trước bằng `parentId`.
2. Hỗ trợ typing indicator, reaction emoji, trạng thái gửi/đã nhận/đã đọc, và mention thành viên bằng `@handle` (tên hiển thị bỏ khoảng trắng hoặc phần local của email). Cùng một người được nhắc nhiều lần trong một tin chỉ nhận một notification.
3. Notification có thể liệt kê, đếm chưa đọc, đánh dấu từng cái hoặc tất cả đã đọc.
4. Upload ảnh/tệp tạo tin nhắn `IMAGE`/`FILE`; giới hạn cấu hình 10 MB mỗi file/request. Tên file lưu được tạo ngẫu nhiên; avatar chỉ nhận MIME type hình ảnh.

## 4. API và bề mặt kiểm thử

| Chức năng | Đường dẫn |
|---|---|
| Trang login/register | `GET /login` |
| Login/register | `POST /api/auth/login`, `POST /api/auth/register` |
| Logout, hồ sơ hiện tại, đổi tên/avatar | `/api/auth/logout`, `/api/auth/me`, `/api/auth/me/avatar` |
| Cuộc trò chuyện, lịch sử, nhóm | `/api/conversations/**` |
| Tìm kiếm người dùng | `/api/users/search?q=...` |
| Bạn bè/yêu cầu kết bạn | `/api/friends/**` |
| Upload/download tệp | `POST /api/upload`, `GET /api/files/{id}` |
| Notification | `/api/notifications/**` |
| WebSocket | `/ws`; prefix client `/app`, topic server `/topic`, đích riêng `/user/queue` |

API được bảo vệ dùng `Authorization: Bearer <token>`. Các phản hồi đã xác nhận gồm `400` dữ liệu thiếu/sai quy tắc, `401` chưa xác thực/sai thông tin đăng nhập, `403` không đủ quyền và `404` không tìm thấy. Kiểm tra từng endpoint riêng; không suy luận mọi API đã có cùng validation hoặc kiểm soát quyền.

## 5. Điều kiện và giới hạn

- Cần JDK 17+, Maven, SQL Server đang chạy, schema ứng dụng sẵn sàng và cấu hình kết nối `DB_URL`/Windows Authentication phù hợp.
- Kiểm thử tích hợp cần user thử; ca nhiều người cần tối thiểu hai tài khoản. Dùng dữ liệu test riêng, không dùng tài khoản production.
- Selenium cần Chrome/ChromeDriver và ứng dụng đang chạy. `LoginTest` hiện `@Disabled`, dùng dữ liệu giả `admin/123` và assertion `/home` hoặc `/dashboard`, không khớp luồng hiện tại; chưa thể xem là UI test pass.
- JUnit mẫu `SampleControllerTest` kiểm tra hàm giả lập, không gọi controller/service ứng dụng.
- Chưa xác nhận Google OAuth end-to-end. Performance/load, security penetration, phục hồi dữ liệu và cross-browser nằm ngoài phạm vi hiện tại.
