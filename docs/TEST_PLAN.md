# KẾ HOẠCH KIỂM THỬ — CHATBOX THESTARS

## 1. Mục tiêu

- Kiểm tra các luồng đăng ký/đăng nhập, nhắn tin, nhóm, bạn bè, hồ sơ, tệp đính kèm và notification theo `SPECIFICATIONS.md`.
- Xác nhận status/response, thay đổi dữ liệu và phân quyền; phát hiện lỗi hồi quy trước khi phát hành.
- Phân biệt unit test mẫu, API/integration test, UI test và WebSocket test; không xem kiểm thử giả lập là bằng chứng cho toàn hệ thống.

## 2. Phạm vi

**Trong phạm vi:** API xác thực/hồ sơ; cuộc trò chuyện đơn/nhóm, vai trò OWNER/DEPUTY/MEMBER; lịch sử và trạng thái đọc; tin nhắn/reply/reaction/mention; quan hệ bạn bè; notification; upload/download; giao diện chính và WebSocket khi môi trường cho phép.

**Ngoài phạm vi:** kiểm thử tải/hiệu năng, kiểm thử xâm nhập chuyên sâu, Google OAuth end-to-end, khả năng phục hồi dữ liệu, mọi trình duyệt/thiết bị. Chính sách chưa có trong code (ví dụ độ mạnh tối thiểu của password hoặc bắt buộc Gmail) không được kiểm thử như quy định bắt buộc.

## 3. Cấp độ và phương pháp

1. **API/service integration:** gửi HTTP request với fixture và Bearer token; kiểm tra status, JSON, quyền truy cập và dữ liệu SQL.
2. **UI smoke/regression:** dùng Chrome kiểm tra đăng nhập, đăng ký, chuyển trang và lỗi form. Chỉ bật Selenium sau khi chuẩn hóa base URL và tài khoản fixture.
3. **WebSocket integration:** hai user cùng mở conversation; xác nhận tin nhắn, typing, reaction, read receipt và mention.
4. **Kiểm thử thủ công:** sử dụng hai/ba tài khoản test riêng cho quan hệ bạn bè và quyền nhóm; dọn dữ liệu sau khi chạy.

## 4. Môi trường và dữ liệu

- Java 17+, Maven, SQL Server với database/schema ứng dụng, Chrome/ChromeDriver; cổng mặc định 8080.
- URL có thể là `http://localhost:8080/login` hoặc `http://localhost:8080/chatbox/login` nếu deploy context path `chatbox`.
- Cấu hình DB theo `application.properties`/`DB_URL`; Windows integrated authentication cần native JDBC auth đúng JVM Tomcat.
- Tạo tối thiểu Alice (OWNER), Bob (MEMBER), Carol (người nhận/yêu cầu kết bạn); đăng ký mật khẩu qua form, không lưu credential vào tài liệu.
- Tạo group fixture có Alice và Bob; chuẩn bị ảnh và file thường nhỏ hơn 10 MB. Ghi ID fixture trong báo cáo từng lần chạy, không hard-code ID phụ thuộc môi trường.
- Token được lưu trong bộ nhớ: đăng nhập lại sau mỗi lần restart. Không chạy thao tác ghi/xóa lên DB thật hoặc môi trường dùng chung.

## 5. Điều kiện bắt đầu và hoàn thành

**Bắt đầu:** ứng dụng khởi động không lỗi; DB và schema kết nối được; fixture sẵn sàng; login API trả token. UI/WebSocket test chỉ bắt đầu khi trình duyệt và kết nối realtime khả dụng.

**Hoàn thành:** mọi ca được ghi Pass/Fail/Blocked theo thực tế; không để ca chưa chạy là Pass; luồng hợp lệ không phát sinh 5xx; dữ liệu sai bị xử lý theo đặc tả; quyền nhóm đúng; báo cáo có URL/context path, build/commit, DB test, lỗi và bước tái hiện. `mvn test` pass không thay thế integration/UI test.

## 6. Rủi ro và lưu ý

- Sai cấu hình SQL Server/native auth hoặc thiếu schema có thể chặn test tích hợp.
- `LoginTest` đang `@Disabled`, dùng account giả và assertion redirect chưa khớp app; JUnit mẫu kiểm tra hàm giả lập. Sửa các test này trước khi dựa vào chúng trong CI.
- Một số endpoint chưa được xác nhận đầy đủ về validation/authorization; ghi nhận kết quả quan sát, không tự giả định.
- Đăng nhập legacy có thể nâng password lên BCrypt; chỉ kiểm tra trên fixture test cô lập.

## 7. Sản phẩm bàn giao

- Trạng thái thực thi từng Test Case trong `TEST_CASES.md`.
- Khi lỗi: bước tái hiện, response đã ẩn token/PII, log liên quan và screenshot nếu là lỗi UI.
- Không ghi password, token, connection string có credential hoặc thông tin cá nhân thật vào log/artifact.
