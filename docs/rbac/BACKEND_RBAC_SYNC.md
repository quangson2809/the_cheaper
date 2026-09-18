# Đồng bộ các dependency RBAC trực tiếp của Order

Thay đổi trên `feature/order-rbac-refactor`, bắt đầu từ
`1fc807ac83e6e464d5cd2096519aa0f03ff9bc51`; base `main` là
`4c030fb40789751d931e09f2258e497692145d60`. Đây là cập nhật sau Phase 6.
Kết quả CI 179 test trước đây không phải bằng chứng cho bản sửa này.

## Thay đổi

- Bốn API dashboard dùng `DASHBOARD_READ` hoặc `ROLE_ADMIN`; kiểm tra ở controller
  và service, không đổi truy vấn/tính toán. JWT thiếu hoặc account bị khóa: 401;
  USER/staff không có quyền: 403; staff được cấp quyền và ADMIN: được truy cập.
- `PermissionCatalog` là nguồn duy nhất cho các định nghĩa permission. Runner
  `OrderPermissionSeeder` giữ tên để tương thích nhưng seed toàn bộ catalog ở mọi
  profile. `DataSeeder` chỉ tạo system role và fixture, không gán lại permission.
- `ADMIN`/`USER` không được đổi tên hoặc xóa; không cho tạo/đổi custom role thành
  tên system role kể cả khác hoa/thường. Có thể sửa description mà giữ nguyên tên.
  Quyền ADMIN được chuẩn hóa thành `ROLE_ADMIN` cả với tên legacy `admin`.
- Khóa/xóa account không được loại quản trị viên hoạt động cuối cùng. Khóa hàng
  role ADMIN rồi đọc khóa các account ADMIN hoạt động trong cùng transaction để
  tuần tự hóa hai thao tác đồng thời. Không dùng ID cố định. Account ADMIN đã khóa
  không được tính là principal dự phòng. API gán role hiện chỉ thêm liên kết,
  không thay/xóa role cũ. Không có API thu hồi role account trong phạm vi này.
- Code permission trong catalog không được đổi hoặc xóa qua API. Thu hồi quyền
  bằng liên kết `role_permissions`; không xóa định nghĩa quyền. Custom permission
  vẫn dùng API CRUD hiện có.
- Controller quản lý account/role/permission có nhánh ADMIN rõ ràng. ADMIN vẫn
  phải qua JWT/account-active, ownership cá nhân và validation nghiệp vụ.

## Catalog và chuyển quyền legacy

| Nhóm | Code |
|---|---|
| Dashboard | `DASHBOARD_READ` |
| Account | `ACCOUNT_READ`, `ACCOUNT_CREATE`, `ACCOUNT_DELETE`, `ACCOUNT_ROLE_READ`, `ACCOUNT_ROLE_UPDATE`, `ACCOUNT_STATUS_UPDATE` |
| Role | `ROLE_READ`, `ROLE_CREATE`, `ROLE_UPDATE`, `ROLE_DELETE` |
| Role-permission | `ROLE_PERMISSION_READ`, `ROLE_PERMISSION_UPDATE`, `ROLE_PERMISSION_GRANT`, `ROLE_PERMISSION_REVOKE` |
| Permission | `PERMISSION_READ`, `PERMISSION_CREATE`, `PERMISSION_UPDATE`, `PERMISSION_DELETE` |
| Admin Order | `ORDER_READ`, `ORDER_CONFIRM`, `ORDER_CANCEL`, `ORDER_DELIVERY_UPDATE`, `ORDER_PAYMENT_COLLECT` |
| Cá nhân | `USER_ORDER_READ`, `USER_ORDER_CREATE`, `USER_ORDER_CANCEL` |
| Legacy, không được runtime dùng để cấp thao tác | `ORDER_UPDATE`, `ACCOUNT_UPDATE`, `ACCOUNT_ASSIGN_ROLE`, `ROLE_ASSIGN_PERMISSION` |

Tổng 31 code. `ORDER_PAYMENT_COLLECT` là định nghĩa dành cho luồng tiếp nối,
chưa có API thu tiền. Catalog không đồng nghĩa một permission đã có endpoint.
`PermissionCatalogConsistencyTest` quét mọi `@PreAuthorize` trong main source và
các code của `OrderAccess`, đối chiếu catalog và kiểm tra legacy không được dùng.

Chính sách migration có chủ đích: **giữ row và liên kết legacy, không alias hay
copy grants sang code mới**. Legacy đã không thỏa mãn controller baseline nên
việc giữ chúng inert không thu hồi một quyền runtime đang hoạt động ở baseline.
Mapping dưới đây phục vụ xét duyệt từng quyền, không phải quy tắc auto-grant:

| Legacy | Các quyền cần xem xét riêng |
|---|---|
| `ROLE_ASSIGN_PERMISSION` | `ROLE_PERMISSION_READ` (đọc), `ROLE_PERMISSION_UPDATE` (thay toàn bộ), `ROLE_PERMISSION_GRANT` (thêm), `ROLE_PERMISSION_REVOKE` (thu hồi) |
| `ACCOUNT_ASSIGN_ROLE` | `ACCOUNT_ROLE_READ`, `ACCOUNT_ROLE_UPDATE`; không suy ra `ACCOUNT_STATUS_UPDATE` |
| `ACCOUNT_UPDATE` | Không có thay thế tổng quát; nếu cần thay trạng thái, xét riêng `ACCOUNT_STATUS_UPDATE` |
| `ORDER_UPDATE` | Xét từng `ORDER_CONFIRM`, `ORDER_CANCEL`, `ORDER_DELIVERY_UPDATE`; không wildcard, không tự cấp thu tiền |

Trước rollout: sao lưu; kiểm kê `permissions`, `role_permissions`, account-role và
ít nhất một ADMIN hoạt động. Không có cơ chế tự phục hồi một DB vốn đã mất ADMIN.
Sau khi khởi động, kiểm tra đủ catalog và diff liên kết staff trước/sau phải rỗng.
Ví dụ truy vấn kiểm kê (read-only):

```sql
SELECT r.name AS role_name, p.code
FROM role_permissions rp
JOIN roles r ON r.id = rp.role_id
JOIN permissions p ON p.id = rp.permission_id
ORDER BY r.name, p.code;

SELECT a.id, a.email, a.status
FROM accounts a JOIN account_roles ar ON ar.account_id = a.id
JOIN roles r ON r.id = ar.role_id
WHERE UPPER(r.name) = 'ADMIN';
```

Người quản trị quyết định từng cặp role/code mới, cấp qua API grant hiện có rồi
kiểm tra JWT của staff. Nếu không có quyết định, không thêm liên kết. Giữ lại bản
kiểm kê để thu hồi đúng những cặp vừa cấp khi rollback. Không có startup migration
nào tái áp dụng mapping này, nên quyền staff đã revoke không quay lại sau restart.

Ngoại lệ baseline có sẵn: `USER_ORDER_*` trên role USER được duy trì bởi listener
startup; chúng không phải quyền staff có thể thu hồi vĩnh viễn bằng xóa liên kết.
`OrderPermissionReconciler` tiếp tục loại `ORDER_*` khỏi USER. Các role staff khác
không bị listener/reconciler cấp thêm quyền. `ACCOUNT_READ`/`PRODUCT_READ` không
còn tự gán cho USER; liên kết cũ nếu có được giữ để người quản trị quyết định thu hồi.
ADMIN có mọi permission định nghĩa từ query/bypass, không cần `role_permissions`.

## Quyền hiệu lực cho frontend

`GET /api/auth/me/authorities`, Bearer access JWT, response trong `ApiResponse`:

```json
{
  "status": 200,
  "data": {
    "roles": ["ORDER_MANAGER", "STAFF"],
    "permissions": ["ORDER_CONFIRM", "ORDER_READ", "ROLE_PERMISSION_READ"]
  }
}
```

Hai mảng là union, không trùng, sắp xếp; lấy từ DB của authenticated principal,
không nhận accountId/role làm nguồn quyền. `ROLE_PERMISSION_*` là permission,
không được lọc bỏ chỉ vì bắt đầu bằng `ROLE_`. ADMIN thấy toàn bộ permission catalog
và custom permission; legacy vẫn có thể xuất hiện như authority lưu trong DB nhưng
không bật bất kỳ thao tác nào theo mapping ngầm. Thiếu JWT/khóa account: 401.
Quyền revoke có hiệu lực ngay với JWT cũ ở request kế tiếp. Frontend tải lại quyền
khi đăng nhập/refresh trang hoặc khi nhận 403; API vẫn kiểm tra mỗi request.
Field `role: String` cũ giữ để tương thích hiển thị, không dùng làm authorization.

## Contract Order giữ nguyên

| Điểm khác biệt | Baseline được giữ và kiểm thử |
|---|---|
| Tạo đơn | HTTP 201 nhưng `ApiResponse.status = 200`; frontend dùng HTTP status |
| Tổng tiền | User `finalAmount`, admin `finalTotal`, cùng giá trị |
| `items[].unitPrice` ở user | Thành tiền dòng `price × quantity`, không phải đơn giá; `price` là đơn giá |
| Phân trang | Request từ `page=1`; Spring Page response `number=0` cho trang đầu |

Không đổi DTO trong bản sửa này. Detail/cancel cá nhân tiếp tục dùng
`findByIdAndAccountId`; không nhận owner từ request. `OrderAccess`, business
validation, `OrderEntity.transitionTo` và `@Version` trên Order/Variant giữ nguyên.
HTTP 409 không tự retry mutation; client reload state trước khi quyết định gửi lại.

## Giới hạn nghiệp vụ vẫn mở

- BIZ-01: non-COD vẫn đánh dấu paid ngay; cần xác minh gateway, idempotency,
  webhook và thiết kế payment transaction. Chưa có API `ORDER_PAYMENT_COLLECT`.
- BIZ-04/05/06: checkout trừ stock/tăng sold; cancel chỉ đổi trạng thái, chưa hoàn
  stock/sold. Cần chốt reservation, xuất/hoàn đúng một lần, dữ liệu cũ và cạnh tranh.
- Giao thất bại, refund/return vẫn là công việc riêng; không thêm trạng thái.

TODO tại `OrderService` tham chiếu các issue này. Test checkout/cancel xác nhận
baseline đang tồn tại, không chứng minh business target mới đã hoàn thành.

## Kiểm chứng

Các test thêm: `DashboardAuthorizationIntegrationTest`, `PermissionCatalogConsistencyTest`,
`AdminSystemRoleInvariantTest`, `OrderAuthorizationRegressionTest`, `AuthoritiesIntegrationTest`,
`LastAdminConcurrencyIntegrationTest`, `SystemRoleGuardTest`, `SystemCatalogInvariantTest`.
`Phase5RestartIntegrationTest` mở/đóng application thật trên cùng MySQL tạm, kiểm tra
catalog mới thiếu được bổ sung, legacy không mở quyền và quyền staff bị revoke
không trở lại sau hai restart. Ma trận JWT/ownership 401/403/404/400 và concurrency
Order 200/409 cũ tiếp tục nằm trong full suite.

Môi trường Windows hiện tại thiếu Docker engine/WSL. Chạy test ở bản sao thư mục
tạm ASCII vì JDK 17/Gradle worker lỗi classpath khi project nằm trong đường dẫn có dấu.
Xem kết quả thực thi cuối cùng trong phần dưới; không thay MySQL bằng mock/H2 hoặc
skip integration test để nghiệm thu. Cần chạy `./gradlew clean test` và
`./gradlew clean build` trên môi trường có Docker và Newman trước khi đánh dấu
Definition of Done hoàn tất. Chưa push/merge PR hoặc chạy migration trên DB thật.

Kết quả local của bản sửa ngày 2026-09-18:

| Lệnh/kiểm tra | Kết quả |
|---|---|
| Biên dịch main và toàn bộ test | Pass |
| `clean test --offline --no-daemon --console=plain` | 206 ca: 75 unit pass, 131 integration không khởi tạo được Docker; 0 skip |
| `clean build --offline --no-daemon --console=plain` | Compile/đóng gói pass; task test fail cùng 131 ca integration thiếu Docker |
| So sánh source workspace với bản sao thực thi | Khớp toàn bộ `src` |
| `git diff --check` | Pass |

Báo cáo local (không commit) ở `build/rbac-sync/reports/tests/test/index.html`,
XML ở `build/rbac-sync/test-results/test/`; hai log và `summary.json` nằm trong
`build/rbac-sync/`. `accepted: false`: chưa xác nhận fresh/restart DB, test JWT mới
hay concurrency MySQL đã pass. Newman chưa chạy vì application chưa khởi tạo.
Chỉ sau full suite xanh mới có thể kết luận Core Order RBAC cùng các dependency
RBAC trực tiếp đã đồng bộ; kết luận đó vẫn không nghiệm thu payment/inventory.
