# Kiểm thử backend

## Yêu cầu

- JDK 17; dùng Gradle wrapper 9.3.1 của repo.
- Docker đang chạy để thực hiện integration test (MySQL 8.4 Testcontainers).
- Có mạng để tải dependency và Docker image lần đầu.
- Node.js 22 và Newman: `npm install --prefix .phase5-tools --no-audit --no-fund newman@6.2.1`.
- Python 3 để tổng hợp bằng chứng: `python scripts/phase5_evidence.py` sau `clean build`.

## Các lệnh

Linux/macOS; trên Windows thay `bash gradlew` bằng `.\gradlew.bat`:

```sh
bash gradlew compileJava compileTestJava
bash gradlew unitTest
bash gradlew integrationTest
bash gradlew clean build
```

`test` và `build` vẫn chạy TOÀN BỘ bộ test. `unitTest` loại tag integration;
`integrationTest` chọn tag integration. Không tắt test khi không có Docker: integration phải báo lỗi.

Báo cáo HTML: `build/reports/tests/<task>/index.html`.
JUnit XML: `build/test-results/<task>/`.
Workflow `Backend tests` chạy `clean build` trên push/PR và lưu báo cáo dưới dạng artifact.
Phase 5 chạy thêm collection Postman trên server thật; báo cáo Newman nằm ở `build/phase5/`.
Xem [ma trận Phase 5](rbac/PHASE5_TEST_MATRIX.md) và [hướng dẫn Postman](postman/ORDER_API_POSTMAN_TEST_PLAN.md).

## Cô lập dữ liệu và tác động bên ngoài

Mọi SpringBootTest kế thừa MySqlIntegrationTest: profile test, MySQL container do Spring quản lý,
và EmailService giả lập. ServiceConnection cung cấp URL/credentials từ container, không dùng DB local.
Cấu hình test có URL dự phòng cổng 1 để lỗi sớm nếu bỏ quên cấu hình container.
DataSeeder mẫu bị tắt bởi profile test; OrderPermissionSeeder/Reconciler vẫn chạy trên DB tạm.
Các test tự tạo role/account cần thiết. Không yêu cầu MySQL/Mailtrap thật hoặc secret thật.

Ngoại lệ có chủ đích: `Phase5RestartIntegrationTest` tự mở/đóng toàn bộ ứng dụng với
profile `phase5-restart`, DataSeeder thật và MySQL container riêng sống qua các lần
restart. Nó chỉ đọc cấu hình test, truyền URL/credentials của container bằng tham số;
không dùng DB phát triển. `Phase5PostmanIntegrationTest` chạy server cổng ngẫu nhiên
và Newman qua HTTP thật. Không tái dùng environment Postman sau khi test đã dừng server.

Không dùng @Container trên từng class rồi tái sử dụng Spring context với container đã dừng.
Container được quản lý cùng vòng đời context. Các service integration test rollback sau mỗi test.
Test optimistic locking chạy qua các transaction riêng, tự xóa bản ghi đã tạo.

## Ý nghĩa của bộ test hiện tại

- Fixture/test được cập nhật từ Account.role sang accountRoles và chữ ký CustomUserDetails mới.
- Gán role thêm liên kết, không thay role cũ; kiểm tra cả trường hợp gán trùng.
- Khôi phục test AdminRoleService trước đây bị comment.
- Kiểm tra chữ ký phương thức trong OrderAuthorizationAnnotationTest được sửa.
- Các assertion vào lớp AdminProtectedAccess đã bị xóa khỏi production được thay bằng kiểm thử
  service theo trách nhiệm dữ liệu và kiểm thử Spring method-security thực trên Permission/RolePermission controller.
- Test dashboard tính toán chạy dưới principal ADMIN; test JWT mới kiểm tra cả bốn endpoint,
  grant/revoke, USER/staff/ADMIN, khóa tài khoản và defense-in-depth ở service.
- Integration test checkout hiện ghi nhận hành vi baseline, chưa xác nhận quy tắc xuất kho/sold mới.

Bản sửa sau Phase 6: xem [ma trận RBAC bổ sung và trạng thái chạy](rbac/BACKEND_RBAC_SYNC.md).

## Tài liệu tham chiếu

- [Spring Boot Testcontainers](https://docs.spring.io/spring-boot/4.0/reference/testing/testcontainers.html)
- [Testcontainers MySQL](https://java.testcontainers.org/modules/databases/mysql/)
- [Hợp đồng và các công việc tiếp nối](rbac/ORDER_RBAC_CONTRACT.md)
- [Bắt đầu từ fresh checkout, gồm PowerShell](../README.md)
- [Bàn giao và điều kiện merge](rbac/PHASE6_HANDOFF.md)

## Khi môi trường chưa chạy được

- Kiểm tra `java -version` là JDK 17 và `docker info` thành công. Integration test
  cần Docker engine, không chỉ Docker CLI. Không đổi sang DB local để làm test xanh.
- Thiếu Newman: chạy lệnh npm ở phần yêu cầu trước Gradle. Dùng `.phase5-tools`, không
  cài vào `build/` vì `clean` sẽ xóa nó. Trên PowerShell có thể dùng `npm.cmd`.
- Đường dẫn Windows có dấu có thể làm wrapper/JDK 17 lỗi classpath. Clone vào thư mục
  ASCII như `C:\work\the_cheaper`; CI Linux là môi trường đã kiểm chứng.
- `phase5_evidence.py` đọc báo cáo task `test` sau `clean build`, không dùng output
  của riêng `unitTest`/`integrationTest` để tuyên bố nghiệm thu toàn bộ.
- Artifact CI có hạn 90 ngày. Tải bản lưu khi bàn giao release nếu cần giữ đầy đủ XML/
  HTML/log lâu hơn; JSON tổng hợp đã được commit trong `docs/rbac/evidence/`.
