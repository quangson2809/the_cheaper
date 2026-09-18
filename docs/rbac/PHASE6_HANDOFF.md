# Phase 6 — rà soát, bàn giao và chuẩn bị merge

> Tài liệu này lưu kết quả Phase 6 trước bản sửa. Trạng thái RBAC-01/02/03 và UI-01
> hiện tại xem [đồng bộ backend RBAC](BACKEND_RBAC_SYNC.md); CI cũ không nghiệm thu thay đổi mới.

Nhánh nguồn `feature/order-rbac-refactor`, đích `main`. Rà soát bắt đầu từ `286e0ad`;
main đối chiếu là `4c030fb`. Phạm vi: phân quyền API Order hiện có, kế thừa quyết
định Phase 1 và việc giữ phạm vi đã xác nhận ở Phase 5. Không mở rộng nghiệp vụ mới.

PR bàn giao: [#2 — Refactor Order RBAC](https://github.com/quangson2809/the_cheaper/pull/2),
giữ draft để review các giới hạn còn lại. **Phase 6 bàn giao đã kiểm chứng; merge và
rollout chưa thực hiện.** Điều này không chuyển các mục RBAC/BIZ/OPS còn hoãn thành hoàn tất.

## Kết quả kiểm chứng Phase 6

Code sau dọn lớp cũ tại `0173f1ca644523b3ba95070a158f2fba616a5e87` đã chạy
[CI thành công](https://github.com/quangson2809/the_cheaper/actions/runs/35249636841):
179 JUnit test, 0 failure/error/skip; Newman 45 request/71 assertion, 0 failure;
evidence `accepted: true`. [Artifact đầy đủ](https://github.com/quangson2809/the_cheaper/actions/runs/35249636841/artifacts/10508702455)
và [JSON tổng hợp trong Git](evidence/phase6-0173f1c.json) gắn kết quả với SHA.
Các liên kết tài liệu nội bộ đã được kiểm tra, main/test biên dịch được, source không
còn tham chiếu AuthorizationException. Kết quả CI của head mới nhất luôn xem trên PR.

## Kết quả đối chiếu code với hợp đồng

| Quy tắc | Đường code hiện tại | Bằng chứng |
|---|---|---|
| Xác thực/tài khoản hoạt động | JwtAuthenticationFilter → CustomUserDetailsService → AuthorizationQueryService | Phase5 rejectedAuthentication… với token thật; locked/expired/sai chữ ký trả 401 |
| Tách quyền thao tác | Hai controller + service; OrderAccess.canUpdate | 42 tổ hợp staff, 16 tổ hợp cá nhân; đúng quyền mới được thao tác |
| Khách chỉ tác động đơn mình | OrderAccess.canAccessOwn và findByIdAndAccountId | IDOR, accountId giả, ADMIN/nhiều role trên API cá nhân vẫn giới hạn |
| Nhân viên phạm vi toàn hệ thống | AdminOrderService truy vấn toàn hệ thống sau kiểm tra quyền | Ca thao tác trên đơn khách khác; không có yêu cầu phân công đơn |
| Hợp quyền, cấp/thu hồi tức thời | AuthorizationQueryService hợp role-permission từ DB mỗi request | Token cũ vẫn thay đổi kết quả; còn nguồn cấp khác vẫn có quyền |
| ADMIN cao nhất nhưng không vượt nghiệp vụ | hasRole('ADMIN')/OrderAccess + OrderEntity.transitionTo | Không cần gán từng permission; terminal state/đơn chưa trả tiền vẫn bị chặn |
| Request bị từ chối không đổi dữ liệu | Transaction service + kiểm tra quyền trước thao tác | JDBC snapshot sau request: đơn, items, kho, payment, cart |
| Xung đột không ghi đè âm thầm | @Version trên OrderEntity/ProductVariantEntity + handler 409 | Hai request cùng version: đúng một 200, một 409; version tăng một lần |
| Khởi tạo/nâng cấp/restart | Các seeder/reconciler + guard dữ liệu mẫu | Context đóng/mở thật trên MySQL; không gán lại quyền staff đã thu hồi |

Đọc [ma trận thực thi đầy đủ](PHASE5_TEST_MATRIX.md). Rà soát source này bổ sung cho
test JWT/MySQL; không thay test hành vi bằng so sánh chuỗi annotation.

## Đường cũ đã bỏ và phần giữ có lý do

| Thành phần | Quyết định |
|---|---|
| checkAuthentication(ownerId,userId,role) và admin bypass trên API cá nhân | Đã bỏ ở refactor; ownership nay cố định theo principal, không nhận role từ caller |
| Order.setStatus và helper thay kho public | Đã thay bằng state machine/helper private; không có endpoint cũ bỏ qua quyền |
| AuthorizationException tùy biến | Phase 6 xóa class chết, không còn import/call site trong src; dùng AccessDeniedException/Spring Security |
| ORDER_UPDATE | Giữ row catalog/liên kết staff cũ để kiểm kê/migration; không được kiểm tra như quyền cho thao tác Order; test chỉ có mã này nhận 403 |
| ORDER_PAYMENT_COLLECT | Giữ catalog mục tiêu cho công việc tiếp nối; không có endpoint thu tiền và không cấp quyền đổi status |
| Response Auth/Account.role dạng String | Giữ DTO tương thích; chỉ một role đầu tiên, không dùng làm nguồn quyền hoặc biểu diễn đủ nhiều role |
| OrderService.calculateFinalAmount public | Hàm tính thuần, không ghi trạng thái/kho/thanh toán; không phải đường mutation vượt security |
| AdminProtectedAccess | Không còn trong source nhánh; không khôi phục cơ chế bảo vệ cũ để che khoảng trống các module khác |

Không xóa permission catalog legacy khi chưa có kế hoạch chuyển quyền đã duyệt.
Không thay API, trạng thái hay side effect thanh toán/kho trong Phase 6.

## Bộ tài liệu cho người nhận

| Đầu ra | Người nhận có thể làm gì |
|---|---|
| [README](../../README.md), [TESTING](../TESTING.md) | Clone đúng nhánh, chuẩn bị runtime/Docker, chạy build/test/Newman không dùng DB thật |
| [ORDER_API](../api/ORDER_API.md) | Hiểu đúng endpoint, permission, ownership, trạng thái, response và mã lỗi |
| [DATA_UPGRADE](DATA_UPGRADE.md), SQL preflight/postflight | Kiểm kê DB/quyền, diễn tập nâng cấp, cấp quyền có chủ đích và lên kế hoạch rollback |
| [FRONTEND_CHANGES](../api/FRONTEND_CHANGES.md) | Lên thay đổi UI, xử lý lỗi/role nhiều quyền; không suy đã có API nghiệp vụ chưa làm |
| [FOLLOW_UP](FOLLOW_UP.md) | Tách việc COD, giao thất bại, hoàn trả, xuất/hoàn kho, sold khỏi nghiệm thu hiện tại |
| [PHASE5_RESULTS](PHASE5_RESULTS.md) | Kiểm tra 179 test, 45 request/71 assertion, SHA/run/artifact; 0 lỗi và 0 skip |

Runbook nâng cấp đã đối chiếu với source/schema; **chưa chạy trên database thật của
người dùng**. Linux CI chứng minh fresh checkout/build/Testcontainers; chưa tuyên
bố đã kiểm thử mọi cài đặt Windows hoặc database legacy tùy biến.

## Trạng thái theo đầu ra, không theo số commit

Chuỗi trạng thái: **chưa bắt đầu → đang làm → đã triển khai → đã kiểm chứng**.
`Đã triển khai` nghĩa là có code/tài liệu; `đã kiểm chứng` phải chỉ được bằng chứng.

| Hạng mục | Trạng thái tại bàn giao | Cơ sở / phần chưa đạt |
|---|---|---|
| Phase 1 — hợp đồng Order hiện có | Đã kiểm chứng | Đối chiếu bảng quyền phía trên và test hành vi Phase 5 |
| Phase 2 — nền tảng chạy test | Đã kiểm chứng | CI clean build với MySQL/Newman; không bỏ test khi thiếu Docker |
| Phase 3 — quyền áp dụng cho Order | Đã kiểm chứng trong phạm vi Order | Cấp/thu hồi, hợp quyền, ADMIN, khóa, restart; không nghiệm thu RBAC toàn hệ thống |
| Phase 4 — API Order hiện có | Đã kiểm chứng | JWT/filter/controller/service/DB và hồi quy |
| Phase 5 — ma trận | Đã kiểm chứng | Kết quả 179 JUnit; 45 request/71 assertion; không skip |
| Phase 6 — rà soát, tài liệu, dọn đường cũ | Đã kiểm chứng | Đối chiếu source, liên kết nội bộ, compile và CI tại 0173f1c; bằng chứng ở trên |
| Phase 6 — chuẩn bị PR | Đã kiểm chứng | PR #2 đã tạo đúng base/head, có kết quả test và giới hạn; giữ draft |
| Merge/rollout | Chưa bắt đầu | Chưa có phê duyệt/định phạm vi các điểm chặn; không tự merge |
| Nghiệp vụ tiếp nối và rollout thật | Chưa bắt đầu | Xem từng mục BIZ/RBAC/OPS/UI trong FOLLOW_UP |

Không quy trạng thái các mục hoãn thành hoàn thành nhánh. Reviewer dùng checklist
dưới và kết quả kiểm chứng trên PR để quyết định merge/rollout riêng.

## Checklist để review/merge

- [ ] CI trên head của PR đạt, `evidence.json` accepted=true; các report bắt buộc đầy đủ.
- [x] Bộ tài liệu có đường đi từ README tới API, test, dữ liệu và giới hạn; liên kết hợp lệ.
- [ ] Mapping quyền staff đã được chủ hệ thống duyệt; không auto-expand ORDER_UPDATE.
- [ ] RBAC-01 dashboard, RBAC-02 role ADMIN và RBAC-03 catalog quản trị đã được xử lý
  hoặc quyết định phạm vi/biện pháp cụ thể trên PR. Hiện chưa có quyết định đó.
- [ ] Frontend và các bên triển khai hiểu 403/404/409, nhiều role và nghiệp vụ hoãn.
- [ ] Nếu triển khai dữ liệu thật: đã diễn tập backup/restore/restart trên bản sao;
  cấu hình/secret/tài khoản mẫu được xử lý theo OPS-01/02.
- [ ] Người có thẩm quyền duyệt merge. Agent chỉ chuẩn bị PR, không tự merge.

Kết luận: phần Order đã có bằng chứng hành vi; bàn giao phải giữ các giới hạn ngoài
phạm vi hiện rõ. Chưa kết luận nhánh sẵn sàng production hoặc RBAC toàn hệ thống đã
hoàn thành. PR draft là phương tiện review, không phải phê duyệt các rủi ro còn lại.
