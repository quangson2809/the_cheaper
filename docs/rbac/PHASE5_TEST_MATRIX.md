# Phase 5 — ma trận kiểm thử quyền và hành vi

Phạm vi: nhánh `feature/order-rbac-refactor`, API Order hiện có theo
[Phase 1](ORDER_RBAC_CONTRACT.md). Người dùng xác nhận giữ phạm vi này ngày 17/09/2026.
Thu tiền COD, giao thất bại, hoàn trả và đổi thời điểm trừ kho/tính sold là nghiệp vụ
chưa triển khai; không coi kết quả kiểm thử này là nghiệm thu các API chưa tồn tại.

## Cơ chế kiểm chứng

- `Phase5OrderSecurityIntegrationTest`: JWT có chữ ký thật → JwtAuthenticationFilter →
  Spring method security → controller/service → MySQL 8.4. Không dùng WithMockUser,
  không tắt filter, không thay service/repository bằng mock.
- Không bọc request trong transaction của test. Fixture commit trước request;
  JDBC đọc dữ liệu đã commit sau response, tránh assertion trên entity cache.
- Snapshot trước/sau gồm orders, order_items, product_variants, payments, cart_items.
- Ca cạnh tranh dùng hai thread và hai transaction thật. Spy duy nhất trên DTO mapper
  đặt barrier trước commit để cả hai request đã đọc version 0. Không giả lập lỗi
  optimistic locking: MySQL/Hibernate phải tạo lỗi, HTTP phải trả đúng 409.
- `Phase5RestartIntegrationTest`: khởi động đầy đủ ứng dụng với DataSeeder thật,
  đóng context rồi khởi động hai lần trên cùng MySQL tạm. Không chỉ gọi lại seeder.
- `Phase5PostmanIntegrationTest`: mở server trên cổng ngẫu nhiên, đăng nhập bằng
  mật khẩu fixture qua HTTP, chạy chính collection bàn giao bằng Newman 6.2.1.

## Đối chiếu ma trận bắt buộc

| Nhóm | Ca tự động | Kết quả yêu cầu |
|---|---|---|
| Xác thực | rejectedAuthenticationCannotMutateAnything × 5 | Thiếu, sai định dạng, hết hạn, sai chữ ký, bị khóa: 401 và snapshot không đổi |
| Quyền thao tác | staffPermissionMatrix × 42; customerPermissionMatrix × 16 | Mỗi quyền/endpoint chỉ đúng 200/201 hoặc 403; dữ liệu sau ghi đúng |
| Tách quyền nhân viên | staffPermissionMatrix; confirmPermissionCannotCollectPaymentByInjectingFields | Confirm không hủy/xuất giao; collect/legacy không đổi trạng thái; không ghi paymentStatus bằng payload lạ |
| Chủ sở hữu | personalScopeSurvivesExtraRolesAndForgedAccountId × 3; Postman IDOR | A đọc/hủy B: 404; list chỉ có đơn A; accountId do client đưa không đổi phạm vi |
| Phạm vi nhân viên | staffPermissionMatrix; Postman staff read | Người có quyền thao tác được trên đơn khách khác, đúng phạm vi toàn hệ thống |
| Nhiều role | grantAndRevokeAcrossTwoRolesTakeEffectWithOriginalToken; personalScope… | Hợp quyền; bỏ một nguồn vẫn còn quyền từ role khác; API cá nhân vẫn giữ ownership, cả ADMIN |
| Thay đổi quyền | grantAndRevokeAcrossTwoRolesTakeEffectWithOriginalToken; Phase 4 revokedPermission… | Token cũ: cấp có hiệu lực; thu hồi nguồn cuối bị 403 |
| Tài khoản khóa | rejectedAuthentication…[locked] | Token còn hạn + đúng quyền vẫn 401, không side effect |
| Khởi tạo dữ liệu | freshLegacyAndRepeatedStartupPreserveDataAndRevocation | DB mới có quyền nền; DB cũ bỏ quyền Order sai trên USER, bổ sung catalog/version; hai restart không phục hồi quyền staff đã thu hồi hoặc nhân đôi dữ liệu |
| Toàn vẹn | Snapshot trong mọi ca bị từ chối; invalidCancellationRollsBackAllData × 4 | Đơn/kho/thanh toán/giỏ giữ nguyên cho 401/403/404/400 |
| Cập nhật đồng thời | concurrentConflictingHttpRequestsHaveOneWinnerAndOne409; OrderOptimisticLockingIntegrationTest | Đúng một 200, một 409; version chỉ tăng 1; trạng thái khớp request thắng; kho/payment giữ nguyên |
| Hồi quy | clean build + toàn bộ test cũ + Postman | Login, checkout COD, đọc/hủy, xác nhận/xuất giao/giao đã trả tiền; nghiệp vụ sai trả 400 |

Quyền USER_ORDER_* là quyền nền tảng của role USER, được khôi phục khi startup theo
thiết kế hiện tại. Quyền ORDER_* của nhân viên không được tự gán lại. ADMIN cao nhất
vẫn không vượt trạng thái tài khoản hay điều kiện nghiệp vụ.

## Sửa lỗi đi kèm

DataSeeder trước đây luôn chèn fixture mẫu. Thêm guard bỏ qua phần dữ liệu mẫu khi
đã có tài khoản, sản phẩm, đơn hoặc phương thức thanh toán; vẫn tạo catalog/role nền.
Ca restart kiểm tra số lượng và toàn bộ dữ liệu nghiệp vụ, không chỉ việc startup không ném lỗi.
Đây không phải hệ thống migration tổng quát hoặc xử lý khởi động nhiều instance đồng thời.

## Chạy lại và bằng chứng

```sh
npm install --prefix .phase5-tools --no-audit --no-fund newman@6.2.1
bash gradlew clean build --no-daemon --console=plain
python scripts/phase5_evidence.py
```

Cần JDK 17, Node.js, Python 3, Docker chạy được MySQL 8.4. Trên Windows dùng Gradle
wrapper tương ứng. Không bỏ integration test khi thiếu Docker: chạy phải thất bại.

- JUnit XML: `build/test-results/test/`; HTML: `build/reports/tests/test/index.html`.
- Newman: `build/phase5/newman.json`, `newman.xml`, `newman.log`.
- Tổng hợp có SHA commit và số ca/failure/skip: `build/phase5/evidence.json`.
- CI lưu tất cả trong artifact `backend-test-reports` kể cả khi thất bại.
- Script bằng chứng yêu cầu ba lớp Phase 5 tồn tại, đủ số ca và không có ca bị skip;
  đồng thời kiểm tra kết quả Newman, không coi compile hoặc đọc annotation là đạt.

Trạng thái chạy cuối cùng và liên kết CI sẽ được ghi trong `PHASE5_RESULTS.md` sau
khi kiểm thử thực thi. Không suy ra kết quả từ việc file test đã được viết.
