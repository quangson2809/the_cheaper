# Đánh giá Phase 3 và triển khai Phase 4

## Baseline đã đánh giá

Commit `f508f83cb8ce7009cf90f5a1f7c73caa914ed4c6`.
[CI baseline thành công](https://github.com/quangson2809/the_cheaper/actions/runs/35104531727).

Phase 3 có: catalog quyền Order; nạp permission từ DB mỗi request; ADMIN nhận toàn bộ permission
đang được định nghĩa; không tự gán lại quyền ORDER_* cho nhân viên; chặn tài khoản khóa trong JWT filter.
Test hiện có chứng minh cấp/thu hồi quyền nhân viên, quyền ADMIN không cần liên kết role-permission,
và kiểm tra trạng thái tài khoản. Đây là nền tảng đủ để triển khai Phase 4, không phải nghiệm thu mọi
trường hợp migration/khởi động lại hoặc mọi API của hệ thống.

## Điểm còn hạn chế sau đánh giá

- ADMIN của Phase 3 nhận permission từ catalog; đây chưa phải bypass độc lập với catalog.
  Phase 4 dùng kiểm tra ROLE_ADMIN trực tiếp ở API/service Order, nên không phụ thuộc row permission cho Order.
- Quyền USER_ORDER_* bị gán lại lúc ApplicationReadyEvent. Đây là hành vi quyền khách nền tảng trong code,
  không áp dụng cho quyền nhân viên. Nếu cần thu hồi lâu dài quyền khách, phải thay chính sách baseline này.
- Test chạy lại seeder không tương đương khởi động lại toàn bộ ứng dụng trên DB đã có dữ liệu.
  DataSeeder vẫn tạo dữ liệu mẫu không idempotent; kiểm thử nâng cấp/restart cần được hoàn thiện riêng.
- Bảo vệ tên/role ADMIN khỏi thay đổi qua API quản lý role chưa được chứng minh bởi Phase 3.
  Không kết luận toàn bộ quản trị role an toàn chỉ dựa trên test Order.
- Dashboard và bất đồng mã ROLE_PERMISSION_* / ROLE_ASSIGN_PERMISSION vẫn là việc cần giải quyết trước nghiệm thu RBAC toàn hệ thống.

## API Order được bảo vệ trong Phase 4

| API | Permission |
|---|---|
| POST /api/orders | USER_ORDER_CREATE + accountId từ người đăng nhập |
| GET /api/orders | USER_ORDER_READ + tài khoản hiện tại |
| GET /api/orders/{id} | USER_ORDER_READ + chủ đơn |
| POST /api/orders/{id}/cancel | USER_ORDER_CANCEL + chủ đơn + điều kiện hủy hiện hành |
| GET /api/admin/orders | ORDER_READ hoặc ADMIN |
| GET /api/admin/orders/{id} | ORDER_READ hoặc ADMIN |
| PATCH /api/admin/orders/{id}/status → PROCESSING | ORDER_CONFIRM hoặc ADMIN |
| PATCH ... → DELIVERED | ORDER_CONFIRM hoặc ADMIN |
| PATCH ... → CANCELED | ORDER_CANCEL hoặc ADMIN |
| PATCH ... → SHIPPING | ORDER_DELIVERY_UPDATE hoặc ADMIN |

ORDER_UPDATE cũ không còn cấp quyền cập nhật trạng thái. Không tự chuyển nó thành tất cả quyền mới.
Quyền đọc, xác nhận, hủy, giao hàng độc lập. ORDER_PAYMENT_COLLECT không cấp quyền đổi trạng thái.
ADMIN vẫn phải tuân thủ trạng thái nghiệp vụ; API cá nhân vẫn chỉ lấy đơn của tài khoản hiện tại.

Cả controller và service đều kiểm tra quyền. Service cá nhân đối chiếu accountId với principal,
ngăn caller đưa accountId khác để vượt ownership. Các helper thay đổi trạng thái/kho được thu về private.

## Phản hồi

- 401: thiếu/không hợp lệ token hoặc tài khoản khóa.
- 403: đã xác thực nhưng thiếu quyền. Quyền được kiểm tra trước truy vấn đơn.
- 404: không tìm thấy đơn; với khách bao gồm đơn không thuộc tài khoản.
- 400: sai JSON/enum, thiếu trạng thái, phân trang ngoài page >= 1 và 1 <= limit <= 100,
  hoặc chuyển trạng thái không hợp lệ dù có quyền.
- 409: xung đột optimistic locking; handler hiện có được giữ nguyên.

Client phải đọc HTTP status. Phản hồi lỗi dùng ApiResponse (status/message/data/timestamp/path).

## Giới hạn nghiệp vụ có chủ đích

Giao thất bại chưa có trong enum/luồng hiện hành: không tạo thành công giả.
Quyền ORDER_CONFIRM đã được thiết kế bao gồm kết quả này nhưng API sẽ chỉ được bổ sung cùng luồng giao thất bại.
ORDER_PAYMENT_COLLECT đã có trong catalog, chưa có API thu tiền COD hoàn chỉnh.
Hoàn trả và thay thời điểm trừ kho/tính sold vẫn là công việc tiếp nối.
Không dùng kết quả Phase 4 để khẳng định toàn bộ chu trình COD hoạt động đúng.

## Kiểm chứng

OrderApiAuthorizationIntegrationTest dùng MySQL tạm, JWT thật và MockMvc với security filter:
- 10 tổ hợp quyền/trạng thái (4 được phép, 6 bị từ chối), kiểm tra trạng thái sau request.
- ADMIN không có liên kết permission; vẫn bị chặn chuyển trạng thái sai.
- Khách xem/hủy đơn mình và không truy cập đơn người khác.
- Gọi trực tiếp service không vượt quyền hoặc giả accountId.
- Token thiếu/sai, tài khoản khóa; thu hồi quyền giữ nguyên token.
- Validation 400, ownership/missing 404, forbidden 403.

Đây là kiểm thử tích hợp quyền và API. Test concurrency MySQL có sẵn kiểm tra cập nhật stale bị từ chối;
OrderErrorResponseTest kiểm tra ánh xạ exception đó sang HTTP 409. Hai test này chưa thay thế bài test HTTP cạnh tranh toàn luồng. Xem CI trên commit hiện tại để xác nhận kết quả thực thi.
