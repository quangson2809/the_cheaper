# Thay đổi frontend cần chuẩn bị

So sánh với `main` tại `4c030fb40789751d931e09f2258e497692145d60`.
Nhánh này không sửa repo frontend. API chi tiết: [ORDER_API.md](ORDER_API.md).

| Trước / phụ thuộc cũ | Sau refactor | Việc frontend cần làm |
|---|---|---|
| Suy quyền thao tác từ một role hoặc ORDER_UPDATE | Mỗi thao tác cần permission riêng; ADMIN đặc biệt | Tách nút đọc/xác nhận/hủy/xuất giao; không cấp tất cả khi có ORDER_UPDATE |
| Admin dùng API cá nhân để xem đơn bất kỳ | API cá nhân luôn theo tài khoản đang đăng nhập | Màn quản trị dùng `/api/admin/orders`; bỏ truyền accountId để đổi chủ đơn |
| Service kiểm tra ownership sau findById | Truy vấn theo id và chủ đơn | Xử lý 404 cho đơn người khác như không tìm thấy; không hiện thông tin đơn |
| Lỗi phân tán, có đường trả 501/404 cho nghiệp vụ sai | Lỗi Order chuẩn hóa 400/401/403/404/409 | Cập nhật mapping lỗi; xung đột 409 cần tải lại dữ liệu |
| Phân trang ít giới hạn | page ≥1, limit 1..100 | Giới hạn UI; nhớ response Page.number từ 0 |
| Sửa trạng thái không qua state machine tập trung | PENDING→PROCESSING/CANCELED; PROCESSING→SHIPPING/CANCELED; SHIPPING→DELIVERED nếu đã trả tiền | Dựng menu thao tác theo cả quyền và trạng thái, backend vẫn quyết định cuối |
| Token cũ có thể dùng tiếp sau thay quyền/khóa | Backend đọc lại quyền và trạng thái tài khoản mỗi request | Chấp nhận 403/401 xuất hiện giữa phiên; không coi cache quyền là nguồn quyết định |

Method, URL và các trường request/response Order được giữ; thay đổi trọng tâm là
quyền, ownership, validation, lỗi và điều kiện trạng thái. POST tạo vẫn HTTP 201,
body.status vẫn 200. Tên tiền ở user là `finalAmount`, ở admin là `finalTotal`.
Không thêm API thu tiền, giao thất bại, hoàn trả hoặc xuất/hoàn kho trong đợt này.

## Các trường role tương thích phải hiểu đúng

Login `AuthResponse.role`, `AdminAccountResponse.role`, `UserAccountResponse.role`
vẫn là **một chuỗi role đầu tiên**, có thể null; không phải tập quyền hợp của nhiều
role và không bảo đảm là role có quyền cao nhất. Hai AccountMapper nay lấy chuỗi
này từ accountRoles để giữ DTO cũ sau khi Account.role đã bị bỏ. Backend không dùng
chuỗi trong response đó để quyết định quyền Order.

Access JWT không chứa danh sách permission. Chưa có endpoint quyền hiệu lực riêng
cho frontend trong phạm vi bàn giao; cần chốt API này khi triển khai UI phân quyền.
Không tự suy tất cả permission từ `role` hoặc giải mã JWT rồi coi đó là danh mục
quyền. Không dùng API quản lý role đặc quyền như API quyền cá nhân thay thế.

## Checklist tích hợp UI sau này

- Đăng nhập A/B, list/detail/cancel cá nhân đúng chủ; quản trị có màn riêng.
- Có ORDER_CONFIRM không hiện hủy/xuất giao/thu tiền nếu không có quyền tương ứng.
- Thông báo riêng cho 401, 403, 404, 409; không coi 400/501 là thành công.
- Sau 200 đọc/đồng bộ trạng thái mới; sau 409 tải lại, không tự gửi lặp vô hạn.
- Không có nút thu tiền/giao thất bại/hoàn trả hoạt động giả khi API chưa có.
- Giỏ/checkout không tự retry khi timeout: chưa có bảo đảm chống tạo đơn trùng.

Checklist này **chưa được kiểm thử trên frontend**; 179 test backend và Newman
không thay thế nghiệm thu giao diện.
