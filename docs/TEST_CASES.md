# BỘ KỊCH BẢN KIỂM THỬ — CHATBOX THESTARS

> Các ca dưới đây là kịch bản cần thực thi, chưa có báo cáo kết quả; trạng thái ban đầu là `Not run`. Dùng fixture riêng trong `TEST_PLAN.md`. Ca API bảo vệ cần Bearer token của đúng người dùng.

| ID | Chức năng | Loại | Tiền điều kiện | Bước thực hiện / dữ liệu | Kết quả mong đợi | Trạng thái |
|---|---|---|---|---|---|---|
| TC_AUTH_01 | Đăng ký | Pos | Email chưa tồn tại | `POST /api/auth/register` với email, tên, team, password | 200; tạo user mới, role USER, OFFLINE; password BCrypt | Not run |
| TC_AUTH_02 | Đăng ký trùng email | Neg | Email đã đăng ký | Gửi đăng ký lại cùng email | 400; không tạo user thứ hai | Not run |
| TC_AUTH_03 | Thiếu trường đăng ký | Neg | — | Bỏ lần lượt email/displayName/team/password khỏi JSON | 400; không tạo user | Not run |
| TC_AUTH_04 | Độ dài tên đăng ký | Boundary | — | Gửi tên 70 rồi 71 ký tự | 70 được chấp nhận; 71 trả 400 | Not run |
| TC_AUTH_05 | Xác nhận mật khẩu form | UI/Neg | Mở trang đăng ký | Nhập hai password khác nhau và submit | Client báo không khớp, không gửi đăng ký | Not run |
| TC_AUTH_06 | Đăng nhập đúng | Pos | Tài khoản BCrypt tồn tại | `POST /api/auth/login` bằng email/password đúng | 200; có token/user; trạng thái ONLINE; UI về `/` | Not run |
| TC_AUTH_07 | Password sai | Neg | User tồn tại | Gửi email đúng, password sai | 401; không cấp token | Not run |
| TC_AUTH_08 | Thiếu password | Neg | User tồn tại | Gửi password rỗng hoặc thiếu | 401; không phát sinh 500 | Not run |
| TC_AUTH_09 | Nâng cấp mật khẩu legacy | Migration | Chỉ fixture cô lập có password cũ | Đăng nhập với password đúng; kiểm tra DB | Đăng nhập 200; lưu BCrypt; lần đăng nhập sau vẫn thành công | Not run |
| TC_AUTH_10 | Đăng xuất | Pos | Đang đăng nhập, có token T1 | `POST /api/auth/logout` Bearer T1; gọi API cần auth bằng T1 | Logout success; token bị thu hồi; API bảo vệ từ chối T1 | Not run |
| TC_PROFILE_01 | Xem hồ sơ | Pos/Neg | Có và không có token | `GET /api/auth/me` với token hợp lệ rồi thiếu token | Hợp lệ trả hồ sơ không có password; thiếu token bị từ chối | Not run |
| TC_PROFILE_02 | Đổi tên | Pos | Đã xác thực | `PUT /api/auth/me` với tên có khoảng trắng đầu/cuối | 200; tên lưu đã trim | Not run |
| TC_PROFILE_03 | Tên không hợp lệ | Neg/Boundary | Đã xác thực | Gửi tên rỗng, chỉ khoảng trắng, 71 ký tự | 400; không cập nhật | Not run |
| TC_PROFILE_04 | Avatar sai loại | Neg | Đã xác thực | Upload file MIME `text/plain` tại `/api/auth/me/avatar` | 400; avatar không đổi | Not run |
| TC_PROFILE_05 | Avatar hợp lệ | Pos | Ảnh nhỏ hơn giới hạn | Upload `image/png` | 200; URL avatar được cập nhật, ảnh truy cập được | Not run |
| TC_CHAT_01 | Danh sách cuộc trò chuyện | Pos/Neg | User A tham gia C | `GET /api/conversations` có/không token | Có token chỉ thấy C của A; thiếu token bị từ chối | Not run |
| TC_CHAT_02 | Mở/tạo chat đơn | Pos | A, B tồn tại | A POST `/api/conversations` type SINGLE/userId B hai lần | Cùng một conversation đơn; A và B là participant | Not run |
| TC_CHAT_03 | Tạo nhóm | Pos | A, B tồn tại | A POST type GROUP, name hợp lệ, memberIds=[B] | 200; A là OWNER, B là MEMBER | Not run |
| TC_CHAT_04 | Tên nhóm quá dài | Neg/Boundary | Có token A | Tạo nhóm với tên 71 ký tự | 400; không tạo nhóm | Not run |
| TC_CHAT_05 | Thiếu trường tạo nhóm | Neg | Có token A | Tạo GROUP thiếu name hoặc memberIds | 400; không tạo nhóm | Not run |
| TC_CHAT_06 | Thành viên xem lịch sử | Pos | A tham gia C | GET `/api/conversations/{C}/messages` | 200; trả lịch sử phân trang và đánh dấu đã đọc | Not run |
| TC_CHAT_07 | Người ngoài xem chat | Neg | Carol không tham gia C | GET `/api/conversations/{C}` và `/messages` bằng token Carol | 403; không lộ chi tiết/lịch sử C | Not run |
| TC_CHAT_08 | Tham gia nhóm có sẵn | Pos | Carol chưa tham gia G | Lấy G từ `/api/conversations/available`, POST `/api/conversations/{G}/participants` với Carol | Thành công; G xuất hiện trong danh sách Carol | Not run |
| TC_CHAT_09 | Quyền OWNER/DEPUTY | Permission | Alice OWNER, Bob DEPUTY, Carol MEMBER | OWNER promote/demote; DEPUTY xóa MEMBER rồi thử xóa OWNER/DEPUTY | OWNER thao tác quản lý hợp lệ; DEPUTY chỉ xóa MEMBER; sai quyền bị 403 | Not run |
| TC_CHAT_10 | OWNER rời nhóm | Pos/Boundary | Có OWNER và thành viên khác | OWNER rời, chỉ định người kế nhiệm; thử với nhóm chỉ có OWNER | Ca đầu chuyển quyền; ca sau giải tán nhóm | Not run |
| TC_MSG_01 | Gửi tin nhắn realtime | Pos | Alice và Bob mở C, WebSocket connected | Alice gửi text tới `/app/chat.send/{C}` | Lưu tin; subscriber topic C nhận nội dung | Not run |
| TC_MSG_02 | Người ngoài gửi tin | Neg | Carol không tham gia C | Carol gửi `/app/chat.send/{C}` | Không lưu/phát tin vào C | Not run |
| TC_MSG_03 | Trả lời tin nhắn | Pos | Có message M trong C | Gửi message mới với `parentId=M`, tải lịch sử | Có reply preview người gửi/nội dung M | Not run |
| TC_MSG_04 | Reaction | Pos | M tồn tại; user tham gia C | Gửi emoji reaction cho M; quan sát topic reaction | Reaction được cập nhật và sự kiện được phát | Not run |
| TC_MSG_05 | Mention | Pos/Boundary | Alice, Bob cùng C | Alice gửi `@handleBob` hai lần trong cùng tin | Bob nhận một notification cho message đó | Not run |
| TC_MSG_06 | Đọc notification | Pos | Có notification chưa đọc | POST `/api/notifications/{id}/read`, sau đó `/read-all` | Notification được đánh dấu đọc; unread count cập nhật | Not run |
| TC_FRIEND_01 | Gửi lời mời | Pos | Alice và Bob chưa có quan hệ | Alice POST `/api/friends/requests` với addresseeId Bob | 200; quan hệ PENDING, hướng OUTGOING/INCOMING đúng | Not run |
| TC_FRIEND_02 | Lời mời không hợp lệ | Neg | Alice có token | Gửi cho mình, ID không tồn tại, hoặc gửi lại quan hệ đã có | Bị từ chối; không tạo quan hệ trùng | Not run |
| TC_FRIEND_03 | Chấp nhận lời mời | Pos/Permission | Alice gửi request PENDING cho Bob | Bob accept; thử để Carol accept cùng request | Bob accept thành công; Carol bị từ chối | Not run |
| TC_FRIEND_04 | Xóa quan hệ | Pos/Permission | Có request/friendship A-B | Một bên DELETE `/api/friends/{id}`; người ngoài thử cùng ID | Bên liên quan xóa được; người ngoài bị từ chối | Not run |
| TC_FILE_01 | Upload ảnh và file | Pos | User là participant; file nhỏ hơn 10 MB | POST `/api/upload` với ảnh và file thường | 200; tạo message IMAGE/FILE cùng attachment | Not run |
| TC_FILE_02 | Upload vượt giới hạn | Neg/Boundary | App đang chạy | Upload file lớn hơn 10 MB | Request bị từ chối; không lưu dữ liệu dở dang | Not run |
| TC_FILE_03 | Download ID không tồn tại | Neg | — | GET `/api/files/{id}` ID không tồn tại | 404 | Not run |
| TC_FILE_04 | Lọc attachment | Pos | C có ảnh và file đính kèm | GET attachments với `type=image`, `file`, `all` | Trả đúng danh sách theo loại; `all` gồm images/files | Not run |

## Ghi kết quả

Ghi ngày chạy, build/commit, URL/context path, môi trường DB, người chạy và kết quả thật. Khi Fail, đính kèm bước tái hiện, response đã ẩn token/PII, log liên quan và screenshot lỗi UI. Không đánh dấu Pass chỉ dựa trên `SampleControllerTest` (hàm giả lập) hoặc `LoginTest` (`@Disabled`).
