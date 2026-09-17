# Order RBAC: hợp đồng và phạm vi đã chốt

Áp dụng cho `feature/order-rbac-refactor`. Baseline: `a204582d`.
Đây là thiết kế mục tiêu, không phải tuyên bố các quyền đã được triển khai.

Tình trạng bàn giao hiện tại: [Phase 6](PHASE6_HANDOFF.md). Hành vi API thực tế:
[ORDER_API](../api/ORDER_API.md). Các mục tiêu nghiệp vụ chưa triển khai được tách ở
[FOLLOW_UP](FOLLOW_UP.md); không suy chúng đã hoàn tất từ catalog quyền bên dưới.

## Quyết định đã xác nhận

- Khách chỉ xem/hủy đơn của mình. Không nhận accountId do client cung cấp để quyết định chủ đơn.
- Nhân viên thao tác theo permission được cấp; không bổ sung phân công đơn riêng trong phạm vi hiện tại.
- Xác nhận giao thành công/thất bại cùng thuộc quyền xác nhận đơn.
- Quyền ADMIN cao nhất được hệ thống bảo đảm, không phụ thuộc gán thủ công mọi permission.
- Quyền cao nhất không bỏ qua xác thực, trạng thái tài khoản và điều kiện nghiệp vụ.
- Ưu tiên API; frontend có thể cập nhật sau.

| Permission mục tiêu | Thao tác | Phạm vi |
|---|---|---|
| USER_ORDER_CREATE | Tạo đơn | Tài khoản hiện tại |
| USER_ORDER_READ | Xem đơn | Đơn của mình |
| USER_ORDER_CANCEL | Hủy đơn hợp lệ | Đơn của mình |
| ORDER_READ | Danh sách/chi tiết đơn | Các đơn trong hệ thống |
| ORDER_CONFIRM | Xác nhận đơn, xác nhận giao thành công/thất bại | Các đơn trong hệ thống |
| ORDER_CANCEL | Hủy đơn hợp lệ | Các đơn trong hệ thống |
| ORDER_DELIVERY_UPDATE | Chuyển sang đang giao | Các đơn trong hệ thống |
| ORDER_PAYMENT_COLLECT | Ghi nhận thu tiền COD | Các đơn trong hệ thống |

Tên permission mới là thiết kế triển khai. Không mặc định chuyển ORDER_UPDATE cũ thành tất cả quyền mới.
Quyền xem không tự bao hàm quyền thay đổi. Mọi thao tác cần đồng thời đúng quyền, phạm vi và điều kiện nghiệp vụ.

## Các phase và điều kiện kết thúc

1. Hợp đồng: ma trận và phạm vi được chốt (tài liệu này).
2. Nền tảng test: compile main/test, chạy unit/integration test; báo cáo rõ lỗi cũ và hạn chế kiểm chứng.
3. Nền tảng RBAC: seed/migrate quyền an toàn; quyền nhân viên có thể thu hồi; ADMIN được bảo đảm; kiểm chứng thay đổi quyền/tài khoản khóa.
4. API: phân quyền theo từng thao tác; kiểm tra ownership; không cho đường gọi khác vượt quyền.
5. Ma trận kiểm thử: xác thực, quyền riêng, phạm vi, nhiều role, thu hồi, dữ liệu mới/cũ và request bị từ chối không có side effect.
6. Bàn giao: tài liệu API, hướng dẫn cập nhật dữ liệu, kết quả kiểm thử và PR.

Không đánh dấu phase đã kiểm chứng nếu chưa chạy được các kiểm thử bắt buộc.

## Nghiệp vụ tiếp nối, chưa triển khai trong Phase 2

- Chỉ COD. Cần luồng ghi nhận thu tiền; không coi phương thức khác COD là đã thanh toán.
- Trừ kho khi xuất hàng, sold khi giao thành công. Hoàn kho khi hủy/hoàn trả phải xét đã xuất và đã hoàn chưa.
- Khách không hủy trực tiếp khi đang giao/đã giao; xử lý bằng hoàn trả.
- Không diễn giải quy tắc trên thành cho phép hủy lại đơn ở trạng thái kết thúc.
- Có giao thất bại. Không thêm enum/API trả thành công nếu luồng chưa được triển khai.

Test hiện tại về trừ kho/tăng sold lúc checkout chỉ là regression cho baseline, không phải nghiệm thu quy tắc mới.

## Khoảng trống tại baseline a204582d (lịch sử)

Tình trạng mới nhất: [đánh giá Phase 3 và API Phase 4](PHASE3_REVIEW_PHASE4_API.md).
Danh sách dưới đây ghi nhận baseline, không đại diện trạng thái hiện tại.

- Seed role/quyền chưa bảo đảm thứ tự trên DB mới; restart có thể gán lại quyền đã thu hồi.
- ADMIN bypass và các permission thao tác mới chưa triển khai.
- JWT filter chưa kiểm tra trạng thái khóa trước khi tạo Authentication từ token.
- AdminDashboardController không có kiểm tra role/permission sau khi AdminProtectedAccess bị loại bỏ.
  Các test dashboard chỉ kiểm tra tính toán, không chứng minh API dashboard an toàn.
- Permission ROLE_PERMISSION_* ở controller chưa đồng nhất với ROLE_ASSIGN_PERMISSION trong DataSeeder.
- Chưa có đầy đủ kiểm thử HTTP cho mã 401/403/404/409 và ma trận nhân viên.

Các khoảng trống này không được che bằng việc bộ test Phase 2 chạy xanh; phải được xử lý/định phạm vi rõ trước merge.
