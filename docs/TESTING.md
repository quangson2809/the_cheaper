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
- Test dashboard là test tính toán. Thiếu authorization ở dashboard là vấn đề được ghi riêng,
  không được coi là đã khắc phục trong Phase 2.
- Integration test checkout hiện ghi nhận hành vi baseline, chưa xác nhận quy tắc xuất kho/sold mới.

## Tài liệu tham chiếu

- [Spring Boot Testcontainers](https://docs.spring.io/spring-boot/4.0/reference/testing/testcontainers.html)
- [Testcontainers MySQL](https://java.testcontainers.org/modules/databases/mysql/)
- [Hợp đồng và các công việc tiếp nối](rbac/ORDER_RBAC_CONTRACT.md)
