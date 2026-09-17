# Hợp đồng API Order đang triển khai

Đối chiếu `UserOrderController`, `AdminOrderController`, `OrderAccess`, hai Order
service, DTO và `GlobalExceptionHandler`. Phạm vi nghiệp vụ: API hiện có. Mọi
request bên dưới cần `Authorization: Bearer <accessToken>` của tài khoản hoạt động.
Login: `POST /api/auth/login` với `email`, `password`; token ở `data.accessToken`.

## Ma trận endpoint, quyền và phạm vi

| Method/path | Quyền | Phạm vi/điều kiện | HTTP thành công |
|---|---|---|---|
| POST `/api/orders` | USER_ORDER_CREATE | Giỏ và chủ đơn là tài khoản đang đăng nhập | 201 |
| GET `/api/orders` | USER_ORDER_READ | Chỉ đơn của mình | 200 |
| GET `/api/orders/{id}` | USER_ORDER_READ | Chỉ đơn của mình; khác chủ trả 404 | 200 |
| POST `/api/orders/{id}/cancel` | USER_ORDER_CANCEL | Đơn của mình, COD, PENDING hoặc PROCESSING | 200 |
| GET `/api/admin/orders` | ORDER_READ | Tất cả đơn trong hệ thống | 200 |
| GET `/api/admin/orders/{id}` | ORDER_READ | Tất cả đơn trong hệ thống | 200 |
| PATCH `/api/admin/orders/{id}/status`, PROCESSING | ORDER_CONFIRM | PENDING → PROCESSING | 200 |
| PATCH cùng path, SHIPPING | ORDER_DELIVERY_UPDATE | PROCESSING → SHIPPING | 200 |
| PATCH cùng path, DELIVERED | ORDER_CONFIRM | SHIPPING → DELIVERED, paymentStatus=1 | 200 |
| PATCH cùng path, CANCELED | ORDER_CANCEL | PENDING/PROCESSING → CANCELED | 200 |

Role **ADMIN** đi qua kiểm tra permission Order trực tiếp, không phụ thuộc các row
role-permission; vẫn chịu xác thực, khóa tài khoản, ownership trên API cá nhân và
điều kiện trạng thái. Không có phân công đơn theo nhân viên trong phạm vi này.
`ORDER_READ` không bao hàm thay đổi; quyền thay đổi không tự cấp quyền đọc.

Quyền được hợp từ nhiều role và nạp lại từ DB mỗi request. Cấp/thu hồi permission
có hiệu lực với token còn hạn ở request tiếp theo; thu hồi một nguồn chưa mất quyền
nếu role khác vẫn cấp. Request đã xác thực và đang chạy không bị hủy ngược thời gian.

`ORDER_UPDATE` là mã legacy không mở bất kỳ API thay đổi Order nào.
`ORDER_PAYMENT_COLLECT` chỉ có trong catalog, chưa có API thu tiền; không mở đường
PATCH trạng thái. `ORDER_CONFIRM` chưa có endpoint ghi nhận giao thất bại.

## Request

GET hai danh sách dùng `page` từ 1 (mặc định 1), `limit` 1..100 (mặc định 10).
Danh sách admin có thêm `status` theo enum. Danh sách cá nhân sắp theo createdAt
giảm dần; danh sách admin hiện không cam kết thứ tự. Không có tham số chủ đơn hợp lệ
cho API cá nhân; `accountId` do client gửi không quyết định quyền sở hữu.

Tạo đơn lấy hàng từ giỏ hiện tại; không nhận danh sách hàng, giá, paymentStatus hay
accountId từ request. `paymentMethodId` bắt buộc và phải tồn tại. DTO hiện chưa bắt
buộc receiver/location/phone; frontend nên thu thập đủ thông tin giao hàng.

```json
{"paymentMethodId": 1, "receiver": "Người nhận", "location": "Địa chỉ giao", "phone": "0901234567"}
```

ID=1 chỉ minh họa, phải lấy ID COD thực trong môi trường. PATCH chỉ nhận trạng thái:

```json
{"status": "PROCESSING"}
```

Không có `version`, `If-Match` hoặc idempotency key trong hợp đồng request hiện tại.
Khóa lạc quan ngăn hai transaction ghi đè; chưa ngăn mọi ý định cập nhật từ màn hình
đã cũ nếu request thứ hai bắt đầu sau khi request đầu hoàn tất. Không tự retry POST
checkout hoặc PATCH khi kết quả mạng chưa rõ.

## Response

Envelope: `status`, `message`, `data`, `timestamp`, `path`. Dùng **HTTP status** làm
kết quả chuẩn: POST tạo đơn trả HTTP 201 nhưng `ApiResponse.success` vẫn đặt
`body.status=200`. Response thành công có thể có `path=null`; lỗi Order hiện trả path
tương đối, ví dụ `/api/orders/123`. Không parse `message` hoặc `path` để phân quyền.

| data | Trường |
|---|---|
| User order | id, status, finalAmount, createdAt, receiver, location, paymentMethodCode, items |
| User item | productId, productName, optionValue, quantity, price, thumbnailUrl, unitPrice |
| Admin overview | id, finalTotal, status, createdAt, countItem, location, phone, paymentMethodCode, paymentStatus |
| Admin detail | id, finalTotal, status, createdAt, paymentMethodCode, paymentStatus, receiver, phone, location, items |
| Admin item | productId, quantity, price, productName, thumbnail, optionNames |

`countItem` là số dòng hàng, không phải tổng quantity. Trường user item `unitPrice`
hiện được tính `price × quantity` (tên trường không phản ánh đúng ý nghĩa); không
tự nhân quantity lần nữa. GET danh sách bọc Spring `Page` trong `data`: `content`,
`totalElements`, `totalPages`, `size`, `number`...; `number` trả về bắt đầu từ 0 dù
tham số request `page` bắt đầu từ 1. Không có version trong DTO response.

```json
{"status":403,"message":"Bạn không có quyền thực hiện thao tác này","data":null,"timestamp":"2026-09-17T10:00:00","path":"/api/admin/orders/123/status"}
```

## Lỗi và trạng thái

| HTTP | Tình huống / xử lý frontend |
|---|---|
| 400 | JSON/enum/validation sai, phân trang ngoài giới hạn, thiếu kho hoặc chuyển trạng thái sai; sửa input/đọc lại đơn |
| 401 | Thiếu/sai/hết hạn JWT hoặc tài khoản khóa; dừng thao tác, xử lý lại phiên đăng nhập |
| 403 | Đã xác thực nhưng thiếu permission; không tự thử endpoint khác |
| 404 | Đơn không tồn tại hoặc không thuộc khách; tài nguyên tạo đơn không tồn tại |
| 409 | Xung đột optimistic locking; tải lại đơn/giỏ/kho, để người dùng quyết định thao tác tiếp |
| 501 | Khách hủy đơn không phải COD: luồng này chưa triển khai; không coi là hủy thành công |

DELIVERED/CANCELED/REFUNDED là trạng thái kết thúc trong state machine hiện tại.
Không có API chuyển sang REFUNDED hoặc trạng thái giao thất bại. Target enum hợp lệ
nhưng không hỗ trợ có thể trả 400 cho người có quyền cập nhật; thiếu quyền trả 403.
Đầu vào malformed có thể bị validation từ chối trước method-security; không dựa
vào thứ tự này để suy ra dữ liệu đơn có tồn tại hay không.

Admin cancel hiện không kiểm tra phương thức thanh toán như API khách; chuyển trạng
thái không hoàn tiền/hoàn kho. Checkout hiện trừ stock, tăng sold ngay, và coi phương
thức khác COD là đã trả tiền. Đây là giới hạn baseline cần đọc ở
[việc tiếp nối](../rbac/FOLLOW_UP.md), không phải hợp đồng thanh toán hoàn chỉnh.
