# Cập nhật dữ liệu Order/RBAC

## Phạm vi và quyết định chuyển quyền

Nguồn hỗ trợ: schema đã có nền RBAC nhiều role như `main` tại `4c030fb` hoặc nhánh
Order trước Phase 5. Phải có accounts, roles, permissions, account_roles,
role_permissions, orders, product_variants. Database còn dùng Account.role/role_id
kiểu cũ hơn chưa được nghiệm thu bởi hướng dẫn này; cần migration RBAC riêng trước.

Quyết định đã chốt từ Phase 1, được giữ nguyên trong Phase 6:

| Dữ liệu cũ | Cách xử lý |
|---|---|
| Role nhân viên có ORDER_UPDATE | Giữ liên kết để đối chiếu; **không tự đổi** thành tất cả quyền mới. Người phụ trách duyệt quyền cụ thể theo từng role |
| ORDER_READ | Giữ trên role nhân viên; chỉ cho đọc |
| USER có quyền ORDER_* | Startup xóa 6 mã staff khỏi role USER, gồm ORDER_UPDATE legacy |
| USER thiếu USER_ORDER_CREATE/READ/CANCEL | ApplicationReadyEvent gán lại quyền khách nền tảng |
| Role nhân viên bị thu hồi ORDER_* | Không tự khôi phục khi restart |
| ADMIN | Quyền cao nhất trên Order không cần liên kết thủ công mọi permission; phải giữ tên role chuẩn ADMIN |
| ORDER_PAYMENT_COLLECT | Catalog dự phòng; không dùng để chứng nhận đã triển khai API thu tiền |

Chốt bảng `role ID → quyền được phép` trước khi ghi dữ liệu. Không suy quyền chỉ từ
tên nhân viên, không cấp quyền staff vào USER, không sửa hàng loạt tất cả role có
ORDER_UPDATE. Xóa row ORDER_UPDATE khỏi catalog không phải cách tắt quyền: seeder sẽ
tạo lại; chính sách API đã vô hiệu hóa tác dụng của mã này.

## Preflight trên bản sao database

1. Chọn SHA bản triển khai và ghi lại SHA ứng dụng đang chạy. Dừng các tiến trình ghi
   khi chụp backup nhất quán. Lưu schema, dữ liệu, cấu hình và bảng cấp quyền cũ.
2. Restore backup sang DB rehearsal riêng, kiểm tra restore thành công; không chạy
   test profile hoặc `clean build` vào DB nghiệp vụ.
3. Chạy [preflight chỉ đọc](sql/order-rbac-preflight.sql) với database đã chọn.
   Phải đủ 7 bảng; ghi lại cột version có/không, role USER/ADMIN, danh sách quyền cũ,
   số đơn, tổng stock/sold/payment. Không tự sửa tên role hoặc deduplicate dữ liệu.
4. Duyệt riêng danh sách quyền staff sẽ cấp. Các vấn đề ngoài Order ở
   [FOLLOW_UP.md](FOLLOW_UP.md) phải được quyết định trước rollout, đặc biệt API cấp quyền.

## Schema version và startup

Ứng dụng hiện dùng Hibernate `ddl-auto=update`, chưa có Flyway/Liquibase. Startup có
thể sửa schema trước các CommandLineRunner. Không coi cơ chế này là migration có
rollback hoặc là bảo đảm nâng cấp mọi DB cũ.

Nếu DBA quản lý schema, chuẩn bị hai cột trước startup trong thời gian ngừng ghi.
Ví dụ dưới đây chỉ chạy câu ADD cho cột **chưa tồn tại theo preflight**; không chạy
lại cả block một cách mù quáng trên DB đã có cột:

```sql
-- Chỉ khi orders.version chưa tồn tại:
ALTER TABLE orders ADD COLUMN version BIGINT NULL;
-- Chỉ khi product_variants.version chưa tồn tại:
ALTER TABLE product_variants ADD COLUMN version BIGINT NULL;
-- Sau khi cả hai cột đã tồn tại; giữ nguyên mọi version khác NULL:
UPDATE orders SET version = 0 WHERE version IS NULL;
UPDATE product_variants SET version = 0 WHERE version IS NULL;
ALTER TABLE product_variants MODIFY COLUMN version BIGINT NOT NULL;
```

MySQL DDL có implicit commit. Không dùng `ROLLBACK` để hứa hoàn tác các ALTER.
Sau khi schema được chuẩn bị, có thể khởi động rehearsal với
`SPRING_JPA_HIBERNATE_DDL_AUTO=validate` để phát hiện phần schema không tương thích.
Nếu dùng `update`, phải xem SQL/log Hibernate và kiểm tra lại schema sau startup.

Thứ tự code hiện có:

1. `OrderVersionReconciler` (@Order 90) điền version NULL=0 trên hai bảng.
2. `OrderPermissionSeeder` (100) tạo catalog còn thiếu, không gán staff permission.
3. `OrderPermissionReconciler` (101) xóa staff permission khỏi USER.
4. `DataSeeder` tạo role/catalog nền; bỏ fixture mẫu nếu đã có account, product,
   order hoặc payment method. Có thể bổ sung permission cho ADMIN/quyền nền khác;
   không được hiểu là mọi row RBAC đều bất biến qua restart.
5. ApplicationReadyEvent bảo đảm 3 USER_ORDER_* cho USER.

Không chạy nhiều instance cùng migrate/seed. Chưa kiểm thử startup cạnh tranh.
Đừng đặt profile `test` để né seeder trong production: đó không phải cấu hình
deployment đã nghiệm thu.

## Gán quyền nhân viên đã được duyệt

Các API quản lý role hiện còn bất đồng catalog `ROLE_PERMISSION_*` và mã seed cũ;
không giả định có thể dùng chúng để migrate trên DB mới. DBA có thể thực hiện
transaction gán quyền cụ thể đã được duyệt trên **bản sao trước**, rồi ghi thành
change script riêng cho môi trường. Ví dụ bảo thủ, mặc định không ghi gì:

```sql
SET @approved_role_id = NULL;        -- thay bằng ID role nhân viên đã duyệt
SET @approved_code = NULL;           -- ví dụ 'ORDER_CONFIRM', không phải ORDER_UPDATE
START TRANSACTION;
SELECT r.id, r.name, p.id, p.code
FROM roles r JOIN permissions p ON p.code = @approved_code
WHERE r.id = @approved_role_id AND r.name NOT IN ('USER', 'ADMIN');
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code = @approved_code
WHERE r.id = @approved_role_id AND r.name NOT IN ('USER', 'ADMIN')
  AND p.code IN ('ORDER_READ','ORDER_CONFIRM','ORDER_CANCEL','ORDER_DELIVERY_UPDATE')
  AND NOT EXISTS (SELECT 1 FROM role_permissions rp
                  WHERE rp.role_id=r.id AND rp.permission_id=p.id);
COMMIT;
```

Kiểm tra đúng một role/permission trước khi gán; giữ biên bản quyết định, ID và kết
quả. Ví dụ không cấp COLLECT vì chưa có API. Không thực thi script mutation nào
trên dữ liệu của người dùng trong Phase 6 này.

## Postflight, rollout và rollback

1. Chạy [postflight](sql/order-rbac-postflight.sql): không có version NULL; USER
   không có 6 quyền staff, có đủ 3 quyền khách; danh sách quyền staff đúng bảng duyệt.
2. Đối chiếu snapshot trước/sau của orders, order_items, product_variants và payments.
   Chỉ version NULL được chuẩn hóa; không đổi trạng thái đơn, stock/sold hoặc payment.
3. Với token cũ của staff, thử quyền đã cấp/thu hồi; kiểm tra A không đọc/hủy B, khóa
   tài khoản trả 401. Dùng fixture riêng cho thao tác ghi, không hủy đơn khách thật.
4. Đóng/mở ứng dụng trên DB rehearsal lần nữa; quyền staff bị thu hồi không quay lại,
   quyền nền USER theo chính sách, không có fixture trùng. Lưu log, SQL output,
   SHA và bản đồ quyền cùng bản phát hành.
5. Chỉ rollout sau khi rehearsal của **backup thực tế** đạt và các điểm chặn merge
   đã được xử lý/định phạm vi. CI chỉ chứng minh fixture DB mới và mô phỏng DB cũ,
   không chứng minh backup của từng môi trường.

Nếu thất bại: dừng traffic ghi, giữ log và snapshot lỗi, phục hồi **cặp** phiên bản
ứng dụng + backup DB trước nâng cấp theo kế hoạch đã diễn tập. Đánh giá/đối soát
mọi ghi phát sinh sau backup trước khi restore. Không chỉ xóa version hoặc hạ code
mà giữ quyền mới rồi coi đó là rollback an toàn; code cũ có thể hiểu quyền khác.

Đây là runbook đã đối chiếu source. Rollout/rollback trên database thật **chưa thực
hiện**; việc đó cần dữ liệu và cửa sổ triển khai riêng.
