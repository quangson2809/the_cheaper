# Chạy collection Order RBAC — Phase 5

Collection được chuẩn bị tự động bởi `Phase5PostmanIntegrationTest` trên MySQL
Testcontainers mới và server cổng ngẫu nhiên. Không dùng tài khoản/database thật.

```sh
npm install --prefix .phase5-tools --no-audit --no-fund newman@6.2.1
bash gradlew integrationTest --tests '*Phase5PostmanIntegrationTest' --no-daemon
```

## Dữ liệu chính xác trước khi chạy

| Fixture | Dữ liệu |
|---|---|
| userA, userB | Hai tài khoản khác nhau; mỗi role có USER_ORDER_CREATE/READ/CANCEL; giỏ trống |
| admin | Role ADMIN, không liên kết permission thủ công |
| reader/confirm/cancel/delivery/collect/none | Mỗi tài khoản chỉ có quyền tương ứng, hoặc không có quyền |
| Sản phẩm | Một variant, giá 100, stock 20, sold 0 |
| Thanh toán | Một PaymentMethod có code COD, ID lấy từ DB |
| Đơn trả tiền trước | Đơn của B, SHIPPING, paymentStatus=1, để kiểm thử xác nhận giao thành công |

Java xuất `build/phase5/postman.environment.json` với baseUrl, password fixture,
`<actor>Email`, variantId, paymentMethodId và paidShippingOrderId. Collection tự
đăng nhập lấy token; tự thêm 2 sản phẩm vào giỏ A và B rồi tạo hai đơn COD PENDING.
Không có ID bỏ trống hoặc giả định ID=1. Mỗi lần chạy phải dùng fixture mới.

## Kết quả cụ thể

- Login 200; thêm giỏ 201; tạo đơn 201, PENDING, COD, tổng 200, đúng một dòng hàng.
- A đọc/hủy B: chỉ 404. Đọc lại B vẫn PENDING.
- Thiếu quyền: chỉ 403. Thiếu/sai token: chỉ 401.
- Hủy đơn A hợp lệ: chỉ 200 và CANCELED; GET tiếp theo phải vẫn CANCELED.
- Hủy lại: 400; dữ liệu không đổi.
- Xác nhận B: 200 PROCESSING; xuất giao: 200 SHIPPING; đọc lại từng bước.
- Khách hủy đơn SHIPPING hoặc giao đơn COD chưa thu tiền: 400; vẫn SHIPPING.
- Nhân viên delivery không có quyền xác nhận giao: 403.
- Confirm giao đơn đã trả tiền: 200 DELIVERED; đọc lại vẫn DELIVERED.
- ADMIN không được đổi trạng thái đơn kết thúc: 400.
- JDBC sau collection: 3 đơn, lần lượt CANCELED/SHIPPING/DELIVERED;
  stock=16, sold=4, không phát sinh payment record. Đây là hồi quy hành vi kho hiện
  có, không phải nghiệm thu quy tắc trừ kho khi xuất hàng/hoàn kho tương lai.

Newman xuất JSON, JUnit XML và log vào `build/phase5/`. Bất kỳ assertion thất bại
nào làm test Java và CI thất bại. Không dùng assertion oneOf cho HTTP status.

Mã sinh collection: `generate-collection.cjs`. Sau chỉnh sửa chạy
`node docs/postman/generate-collection.cjs` và commit cả JSON đã sinh.

Collection là một phần của nghiệm thu. Expired JWT, tài khoản khóa, thay đổi role,
restart và concurrency được chứng minh bằng integration test trong
[ma trận Phase 5](../rbac/PHASE5_TEST_MATRIX.md), không giả lập chúng bằng Postman.
Thu tiền COD, giao thất bại, hoàn trả chưa có API và nằm ngoài phạm vi đã xác nhận.
