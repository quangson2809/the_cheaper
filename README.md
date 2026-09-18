# The Cheaper — backend Order/RBAC

Java 17, Spring Boot 4.0.3, Gradle wrapper 9.3.1, MySQL. Nhánh bàn giao:
`feature/order-rbac-refactor`; nhánh đích PR: `main`.

Cập nhật sau Phase 6: [đồng bộ backend RBAC](docs/rbac/BACKEND_RBAC_SYNC.md) bổ sung
dashboard, catalog, invariant system role và API quyền hiệu lực. Chưa nghiệm thu
full suite bản sửa mới trong môi trường thiếu Docker.

Phạm vi đã nghiệm thu trước bản sửa này là phân quyền và hành vi **API Order hiện có**. Không coi
nhánh này là bản hoàn thiện chu trình COD hoặc RBAC toàn hệ thống. Đọc
[bàn giao và điều kiện merge](docs/rbac/PHASE6_HANDOFF.md) trước khi triển khai.

## Lấy code và chạy toàn bộ kiểm thử

Cài JDK 17, Docker đang chạy, Node.js 22/npm, Python 3 và Git. Docker phải truy cập
được image `mysql:8.4`; lần đầu cần mạng tải Gradle/dependency/Newman. Test tự tạo
database tạm, không cần MySQL hoặc thông tin Mailtrap cá nhân.

Linux/macOS:

```sh
git clone --branch feature/order-rbac-refactor https://github.com/quangson2809/the_cheaper.git
cd the_cheaper
java -version
docker info
npm install --prefix .phase5-tools --no-audit --no-fund newman@6.2.1
bash gradlew clean build --no-daemon --console=plain
python3 scripts/phase5_evidence.py
```

PowerShell — dùng đường dẫn không dấu, ví dụ `C:\work\the_cheaper`, để tránh lỗi
đường dẫn ở Gradle wrapper/JDK 17 trên Windows:

```powershell
git clone --branch feature/order-rbac-refactor https://github.com/quangson2809/the_cheaper.git C:\work\the_cheaper
Set-Location C:\work\the_cheaper
java -version
docker info
npm.cmd install --prefix .phase5-tools --no-audit --no-fund newman@6.2.1
.\gradlew.bat clean build --no-daemon --console=plain
py -3 scripts/phase5_evidence.py
```

Phải đạt build, không skip test, `accepted: true`. Kết quả đối chiếu đã lưu: 179
JUnit test và 45 request/71 assertion Postman; khi bổ sung test số ca có thể tăng.
HTML ở `build/reports/tests/test/index.html`; JUnit XML ở `build/test-results/test/`;
Newman và `evidence.json` ở `build/phase5/`. Xem [chi tiết test](docs/TESTING.md).

## Chạy API thủ công trong môi trường phát triển

Chuẩn bị MySQL và một database **riêng cho phát triển**. Cấu hình đang có trong
`src/main/resources/application.properties` là giá trị phát triển cũ; ghi đè bằng
các biến môi trường sau thay vì dùng chúng trên hệ thống thật:

| Biến | Ý nghĩa |
|---|---|
| `SPRING_DATASOURCE_URL` | Ví dụ `jdbc:mysql://127.0.0.1:3306/the_cheaper_dev?serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Tài khoản riêng của DB phát triển |
| `JWT_SECRET` | Chuỗi Base64 của ít nhất 32 byte ngẫu nhiên, riêng cho môi trường |
| `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT` | SMTP thử nghiệm của bạn |
| `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | Thông tin SMTP thử nghiệm; không commit |
| `SERVER_PORT` | Tùy chọn, mặc định 8080 |

Sau khi đặt các biến, chạy `bash gradlew bootRun` hoặc `.\gradlew.bat bootRun`.
Swagger UI: [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
OpenAPI JSON: [localhost:8080/api-docs](http://localhost:8080/api-docs).
Không cần chạy API thủ công để chạy bộ test ở trên.

DB nghiệp vụ trống, profile mặc định: DataSeeder tạo dữ liệu mẫu gồm tài khoản
`admin@gmail.com`, `an.nguyen@gmail.com`, `binh.tran@gmail.com`, mật khẩu mẫu
`123456`. Đây là fixture phát triển, không phải cơ chế cấp tài khoản production.
Nếu DB đã có dữ liệu, seeder bỏ qua fixture; không giả định các tài khoản này tồn
tại. Không dùng profile `test` làm cấu hình triển khai.

## Tài liệu bàn giao

- [Hợp đồng Phase 1](docs/rbac/ORDER_RBAC_CONTRACT.md): ma trận đã chốt.
- [API và mã quyền](docs/api/ORDER_API.md): endpoint, dữ liệu, lỗi, chuyển trạng thái.
- [Cập nhật dữ liệu và quyền cũ](docs/rbac/DATA_UPGRADE.md): preflight, rehearsal, rollout, rollback.
- [Frontend cần điều chỉnh](docs/api/FRONTEND_CHANGES.md).
- [Ma trận kiểm thử](docs/rbac/PHASE5_TEST_MATRIX.md), [kết quả](docs/rbac/PHASE5_RESULTS.md),
  [Postman](docs/postman/ORDER_API_POSTMAN_TEST_PLAN.md).
- [Việc tiếp nối](docs/rbac/FOLLOW_UP.md): nghiệp vụ hoãn và rủi ro ngoài Order.
- [Rà soát Phase 6 và trạng thái bàn giao](docs/rbac/PHASE6_HANDOFF.md).
