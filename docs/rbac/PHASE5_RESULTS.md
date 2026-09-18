# Kết quả Phase 5 — đạt trong phạm vi API Order hiện có

Ngày kiểm chứng: 17/09/2026. Nhánh: `feature/order-rbac-refactor`.
Commit chạy nghiệm thu: `5a835a55fef63664ecb1f04f33ceab94b34552e0`.

| Bằng chứng thực thi | Kết quả |
|---|---|
| Gradle `clean build` (Java 17, MySQL 8.4 Testcontainers) | Thành công |
| Toàn bộ JUnit | 179 test, 0 failure, 0 error, 0 skipped |
| Ma trận Phase 5 mới | 73 ca quyền/hành vi + 1 ca khởi động/nâng cấp/restart + 1 ca Newman |
| Postman/Newman 6.2.1 qua HTTP thật | 45 request, 71 assertion, 0 failure, 0 pending |
| Kiểm tra bằng chứng bắt buộc | `accepted: true`, không có vấn đề |

- [CI nghiệm thu](https://github.com/quangson2809/the_cheaper/actions/runs/35202909043).
- [Artifact đầy đủ: JUnit XML, HTML, Newman JSON/XML/log, evidence.json](https://github.com/quangson2809/the_cheaper/actions/runs/35202909043/artifacts/10488592743).
- [Bản tổng hợp lưu trong Git](evidence/phase5-5a835a5.json).
- [Ma trận yêu cầu ↔ test](PHASE5_TEST_MATRIX.md).
- [Fixture và cách chạy lại Postman](../postman/ORDER_API_POSTMAN_TEST_PLAN.md).

Các ca mới chạy qua JWT filter, Spring Security và database thật. Request bị từ chối
được đối chiếu snapshot dữ liệu sau transaction; hai request xung đột trả một 200,
một 409; kiểm thử restart đóng/mở ứng dụng thật trên cùng database tạm. Không có ca
bắt buộc trong phạm vi đã xác nhận bị bỏ qua hoặc thay bằng kiểm tra annotation.

Đã sửa DataSeeder để restart/DB có dữ liệu không chèn lại fixture mẫu. Không thay
nghiệp vụ hủy/kho/thanh toán để làm test xanh. Hành vi checkout trừ kho/tăng sold
hiện có được kiểm tra như hồi quy baseline.

Người dùng xác nhận giữ phạm vi API hiện có. Thu tiền COD, giao thất bại, hoàn trả,
hoàn kho và đổi thời điểm trừ kho/tính sold chưa có luồng hoàn chỉnh, không nằm trong
kết luận nghiệm thu này. Test payment chỉ chứng minh quyền/field khác không tự cho
phép ghi thanh toán và request bị từ chối không đổi dữ liệu thanh toán.

Artifact CI được giữ 90 ngày; bản tổng hợp JSON và các liên kết commit/run được lưu
trong Git để giữ dấu vết sau khi artifact hết hạn. Log compile local không được dùng
thay cho bằng chứng MySQL/HTTP: các kết quả trên lấy từ CI thành công.
