## 1. TỔNG QUAN DỰ ÁN (Project Overview & Philosophy)

| Thuộc tính | Giá trị |
|---|---|
| Tên dự án | thecheaper – Hệ thống thương mại điện tử thời trang nam |
| Mô tả ngắn gọn | Nền tảng bán quần áo nam trực tuyến, hỗ trợ cả admin và người dùng cuối. Cho phép quản lý sản phẩm, đơn hàng, danh mục, thương hiệu, tài khoản, thống kê (admin) và mua sắm, giỏ hàng, tích điểm, quản lý đơn hàng (user). |

**Tư tưởng cốt lõi**
- Layer Clean Architecture First: Phân tách rõ ràng giữa business logic, controller, repository, service, entity, dto, exception, config.
- Mã nguồn dễ bảo trì: Ưu tiên tính dễ đọc, dễ mở rộng hơn là viết code "thông minh" nhưng phức tạp.
- Hiệu năng chấp nhận được: API phản hồi dưới 300ms, hỗ trợ phân trang và cache.
- Mobile-first & Responsive: Giao diện thân thiện trên cả điện thoại và desktop.

**Đối tượng người dùng**
- Admin: Quản trị viên hệ thống.
- Customer (User): Khách hàng mua sắm thời trang nam.

## 2. KIẾN TRÚC & CÔNG NGHỆ (Architecture & Tech Stack)

### 2.1. Backend

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 17+ |
| Framework | Spring Boot 3.x |
| Kiến trúc | Clean Architecture (Hexagonal/Onion) |
| ORM | Hibernate / Spring Data JPA |
| Database | PostgreSQL 15+ |
| Caching | Redis (session, token blacklist, hot products) |
| Security | Spring Security + JWT (Access/Refresh Token) |
| API Style | RESTful (JSON) |
| Build Tool | Gradle |
| Documentation | SpringDoc OpenAPI (Swagger) |

### 2.2. Frontend

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | TypeScript (Strict mode) |
| Framework | React 18+ |
| State Management | Redux Toolkit / Zustand (tuỳ chọn) |
| Styling | TailwindCSS |
| Routing | React Router v6 |
| HTTP Client | Axios (interceptor cho token refresh) |
| UI Components | Shadcn/ui hoặc tự build (không dùng thư viện UI quá nặng) |

### 2.3. Infrastructure & DevOps

- Môi trường Development: Docker Compose (PostgreSQL, Redis, backend, frontend).
- Môi trường Production: (Chưa quyết định – có thể dùng AWS/Viettel Cloud).
- Logging: SLF4J + Logback (backend), Console + File (frontend).

## 3. TỪ ĐIỂN NGHIỆP VỤ & THỰC THỂ (Domain Definitions & Entities)

### 3.1. Core Entities

| Thực thể | Mô tả | Thuộc tính quan trọng |
|---|---|---|
| Account | Người dùng hệ thống (admin hoặc customer) | id, email, passwordHash, fullName, role (ADMIN/USER), points (tích điểm), createdAt, status |
| Role | Vai trò phân quyền | id, name (ROLE_ADMIN, ROLE_USER) |
| Product | Sản phẩm thời trang | id, name, description, basePrice, discountPercent, categoryId, brandId, thumbnail, isActive |
| Variant | Biến thể sản phẩm (size, màu) | id, productId, sku, priceAdjustment, stockQuantity, attributes (JSON: {size: "M", color: "Đen"}) |
| ProductImage | Hình ảnh sản phẩm | id, productId, imageUrl, isPrimary |
| Category | Danh mục (VD: Áo sơ mi, Quần jeans) | id, name, slug, parentId (hỗ trợ cấp con) |
| Brand | Thương hiệu (VD: Nike, Uniqlo) | id, name, logoUrl |
| Cart | Giỏ hàng của user (1-1 với Account) | id, accountId, createdAt, updatedAt |
| CartItem | Sản phẩm trong giỏ | id, cartId, variantId, quantity |
| Order | Đơn hàng | id, accountId, totalAmount, status (PENDING, PAID, SHIPPING, COMPLETED, CANCELLED), paymentMethod, shippingAddress, pointsUsed, pointsEarned, createdAt |
| OrderItem | Chi tiết đơn hàng | id, orderId, variantId, quantity, unitPrice |
| Address | Địa chỉ giao hàng của user | id, accountId, fullName, phone, street, ward, district, city, isDefault |
| Comment | Đánh giá / bình luận sản phẩm | id, productId, accountId, rating (1-5), content, createdAt |
| Payment | Giao dịch thanh toán | id, orderId, amount, method (COD, VNPay, Momo), status, transactionId, paidAt |

### 3.2. Quan hệ giữa các thực thể (Entity Relationships)

```text
Brand ──1─N── Product ──1─N── Variant ──1─N── CartItem
Category ──1─N── Product            └─────1─N── OrderItem
Product ──1─N── ProductImage
Product ──1─N── Comment
Account ──1─N── Comment
Account ──1─1── Cart
Cart ──1─N── CartItem
Account ──1─N── Order
Order ──1─N── OrderItem
Account ──1─N── Address
Role ──1─N── Account
Order ──1─1── Payment
Variant ──N─N── OptionValue (thông qua bảng trung gian, nếu cần)
```

### 3.3. Trạng thái đơn hàng (Order Status Flow)

```text
PENDING (chờ thanh toán)
   ├──> PAID (đã thanh toán)
   │      └──> SHIPPING (đang giao)
   │             └──> COMPLETED (hoàn thành)
   └──> CANCELLED (hủy)
```

Lưu ý: Chỉ cho phép hủy khi đơn hàng ở trạng thái PENDING hoặc PAID (chưa giao).

### 3.4. Cách tính điểm (Points)

- Tích điểm: Mỗi 100.000 VNĐ được 1 điểm = 1.000 VNĐ giảm giá (tỷ lệ có thể thay đổi).
- Tiêu điểm: Tối đa sử dụng 30% tổng giá trị đơn hàng.
- Quy định: Điểm không có giá trị quy đổi thành tiền mặt, chỉ dùng cho đơn hàng sau.

## 4. CÁC LUỒNG XỬ LÝ CHÍNH (Core Workflows)

### 4.1. Luồng xác thực (Authentication)

- User gửi email và password đến API /api/auth/login.
- Backend xác minh, trả về accessToken (JWT, hết hạn 15 phút) và refreshToken (lưu trong HTTP-only cookie, hết hạn 7 ngày).
- Mọi request API (trừ login/register) phải gởi kèm header: Authorization: Bearer <accessToken>
- Khi accessToken hết hạn, client gọi /api/auth/refresh để lấy token mới.
- Nếu refreshToken hết hạn, hệ thống buộc người dùng đăng nhập lại.

### 4.2. Luồng đặt hàng (Checkout)

- User xem giỏ hàng thông qua API GET /api/cart.
- User chọn địa chỉ giao hàng đã lưu hoặc thêm địa chỉ mới.
- Thanh toán COD: Đặt hàng thành công, trạng thái PENDING, chờ admin xác nhận.
- Thanh toán VNPay / Momo: Chuyển sang cổng thanh toán. Sau khi thanh toán, hệ thống nhận webhook và cập nhật trạng thái PAID.
- User có thể nhập điểm tích lũy (nếu muốn), hệ thống sẽ tính lại totalAmount.
- Hệ thống tạo Order và trừ stock (tồn kho) của từng variant tương ứng.
- Hệ thống trả về thông tin đơn hàng và payment URL (nếu thanh toán online).

### 4.3. Luồng quản lý sản phẩm (Admin)

- Quyền hạn Admin bao gồm việc thêm, sửa, xoá sản phẩm, biến thể và hình ảnh.
- Admin cập nhật số lượng tồn kho (stock).
- Admin bật/tắt trạng thái hiển thị của sản phẩm (isActive).

### 4.4. Luồng tìm kiếm & lọc (User)

- Tìm kiếm: Hỗ trợ tìm kiếm theo tên sản phẩm (full-text search cơ bản).
- Lọc: Cho phép lọc theo danh mục, thương hiệu, khoảng giá và size (thông qua variant).
- Sắp xếp: Hỗ trợ sắp xếp theo mới nhất, giá từ thấp đến cao, và bán chạy.
- Performance: Hệ thống áp dụng cache kết quả tìm kiếm phổ biến trong Redis (TTL 5 phút).

## 5. CẤU TRÚC THƯ MỤC (Directory Structure)

### 5.1. Backend (Spring Boot – Clean Architecture)

| Layer | Vai trò |
|---|---|
| Controller | Nhận request, gọi service, trả về DTO. |
| Service | Xử lý nghiệp vụ, gọi repository, phối hợp các domain. |
| Repository | Giao tiếp với database (Spring Data JPA). |
| Entity | Ánh xạ bảng database. |
| DTO | Định dạng dữ liệu vào/ra API (tuyệt đối không để lộ Entity). |
| Exception | Chứa Custom exceptions và Global handler. |
| Config | Cấu hình bảo mật, Swagger, các cổng thanh toán. |

## 6. QUY CHUẨN CODE (Coding Rules & Conventions)

### 6.1. Quy tắc chung

- Ngôn ngữ Backend: Sử dụng Java 17. Không dùng var nếu kiểu dữ liệu không rõ ràng.
- Ngôn ngữ Frontend: Sử dụng TypeScript Strict mode. Cấm sử dụng any – ưu tiên unknown kết hợp type guard.
- Naming Convention Biến/Hàm: Sử dụng camelCase (Java: getUserById, TS: fetchProducts).
- Naming Convention Class/Interface/Component: Sử dụng PascalCase (ProductService, CartPage).
- Naming Convention Hằng số: Sử dụng UPPER_SNAKE_CASE (MAX_RETRY_COUNT).
- Naming Convention File React: Sử dụng PascalCase.tsx (ProductCard.tsx).
- Comment Code: Viết bằng tiếng Việt hoặc tiếng Anh một cách nhất quán toàn dự án. Khuyến khích sử dụng tiếng Anh cho code, Javadoc và JSDoc.

### 6.2. Backend Conventions

Response format chuẩn (dùng chung cho mọi API):

```json
{
  "success": true,
  "data": {},
  "message": "Thành công",
  "error": null
}
```

Ghi chú Response: data có thể là object {} hoặc mảng [], chỉ xuất hiện khi success=true. error chứa mã lỗi hoặc chi tiết, chỉ xuất hiện khi success=false.

Xử lý tiền tệ: Tất cả trường liên quan đến tiền (giá, tổng đơn hàng, điểm) bắt buộc dùng BigDecimal trong Java. Tuyệt đối không dùng float hoặc double.

Kiểu dữ liệu Database (Tiền tệ): Sử dụng numeric(12,0) (lưu số VNĐ, đơn vị đồng).

Validation: Sử dụng Jakarta Validation (@NotNull, @Min, @Max, @Pattern).

Transaction: Các method trong Service có thao tác chỉnh sửa nhiều bảng bắt buộc phải có annotation @Transactional.

### 6.3. Frontend Conventions

- Tách Logic & UI: Component chỉ đảm nhận việc nhận props và render giao diện.
- Xử lý Logic: Nghiệp vụ gọi API, xử lý state phải được đặt trong các Custom Hooks (useProductList, useCart).
- Styling: Chỉ dùng Tailwind CSS. Không viết CSS thuần trừ khi thực sự cần thiết (ví dụ: animate phức tạp). Tuyệt đối không dùng !important.
- State Management (Global): Sử dụng Redux Toolkit hoặc Zustand cho auth, cart.
- State Management (Local/Server): Sử dụng React Query (TanStack Query) để lưu cache dữ liệu từ API.

### 6.4. Database Conventions

- Tên bảng: Định dạng snake_case, số nhiều (ví dụ: accounts, products, order_items).
- Khóa chính: Đặt tên là id (SERIAL/BIGINT), ngoại trừ các bảng trung gian có composite key.
- Timestamp: Mỗi bảng bắt buộc có created_at (TIMESTAMP) và updated_at (tự động cập nhật).
- Soft delete: Hệ thống không xóa vật lý. Sử dụng cột deleted_at hoặc is_active để quản lý.

### 6.5. SOLID Principles (BẮT BUỘC)

Backend (Java/Spring) & Frontend (React/TS) đều phải tuân thủ nghiêm ngặt 5 nguyên tắc SOLID:

| Nguyên tắc | Ý nghĩa | Ví dụ áp dụng |
|---|---|---|
| S – Single Responsibility | Một class/function/component chỉ có một lý do để thay đổi. | Không nhét logic gọi API, render UI, xử lý form vào cùng một component. Tách service, controller, repository rõ ràng. |
| O – Open/Closed | Mở để mở rộng, đóng để sửa đổi code gốc. | Dùng strategy pattern cho các phương thức thanh toán (VNPay, Momo, COD) thay vì if-else. Dùng hook custom để mở rộng behavior. |
| L – Liskov Substitution | Kiểu con phải thay thế được kiểu cha mà không phá vỡ chương trình. | Không override method rồi ném ra ngoại lệ không mong muốn. Không ép kiểu con implement method vô nghĩa. |
| I – Interface Segregation | Interface nhỏ, không ép class implement phương thức không dùng. | Chia UserService thành AuthService, ProfileService, OrderService. Tránh interface "God". |
| D – Dependency Inversion | Phụ thuộc vào abstraction (interface), không phụ thuộc vào concrete class. | Service gọi repository qua interface, không new trực tiếp. Dùng dependency injection (Spring @Autowired, React context/hook). |

Kiểm tra SOLID: Mỗi pull request phải tự đánh giá sự tuân thủ các nguyên tắc này.

Dấu hiệu vi phạm S: Cần sửa nhiều file không liên quan cùng một lúc.

Dấu hiệu vi phạm O: Muốn thêm tính năng mới nhưng bắt buộc phải sửa class cũ.

## 7. XỬ LÝ LỖI & LOGGING (Error Handling)

### 7.1. Backend

- BusinessException: Xử lý lỗi nghiệp vụ (hết hàng, sai điểm, trạng thái đơn hàng không hợp lệ).
- ResourceNotFoundException: Xử lý lỗi không tìm thấy tài nguyên (sản phẩm, user).
- AuthenticationException: Xử lý lỗi bảo mật (sai password, token hết hạn).
- Global Exception Handler: Sử dụng @RestControllerAdvice để bắt tất cả exception và trả về response chuẩn với success=false.
- Logging Backend: Bắt buộc dùng SLF4J + Logback. Nghiêm cấm sử dụng System.out.println(). Tuyệt đối không log password hoặc token vào file.

### 7.2. Frontend

- API Error Interceptor: Khi nhận response có success=false, hệ thống phải hiển thị toast thông báo phù hợp.
- Logging Development: Cho phép sử dụng console.log.
- Logging Production: Bắt buộc gửi thông tin lỗi về backend (thông qua Sentry hoặc custom endpoint).

## 8. CHỈ THỊ CỐT LÕI DÀNH CHO AGENT (Agent Core Directives)

Các mệnh lệnh dưới đây bắt buộc Agent phải tuân theo khi tương tác với dự án.

### 8.1. [THINKING PROCESS]

Trước khi viết code, Agent phải phân tích nghiệp vụ và kiến trúc. Trình bày các bước giải quyết bằng cách:
- Liệt kê các domain entity liên quan.
- Xác định luồng dữ liệu chính.
- Đề xuất vị trí đặt code (feature nào, layer nào).
- Chỉ bắt đầu sinh code sau khi đã được xác nhận (hoặc tự đánh giá đủ rõ ràng).

### 8.2. [NO HALLUCINATION]

Nếu thiếu ngữ cảnh về tên một file/hàm/class, cấu trúc một bảng trong database, hoặc một thư viện chưa từng được nhắc đến:
- Agent phải hỏi lại người dùng.
- Tuyệt đối KHÔNG TỰ BỊA RA tên hàm hay tên cột.

### 8.3. [DRY PRINCIPLE]

Trước khi tạo hàm tiện ích (utility) mới:
- Kiểm tra thư mục utils/ (frontend) hoặc shared/ (backend) xem đã có hàm tương tự chưa.
- Ưu tiên mở rộng hàm hiện có thay vì viết mới hoàn toàn.

### 8.4. [TESTING]

Mọi core logic mới (use case, service, custom hook phức tạp) đều bắt buộc phải có unit test:
- Backend: Sử dụng JUnit 5 + Mockito.
- Frontend: Sử dụng Vitest + React Testing Library.

Yêu cầu tối thiểu: Phải test "happy path" và ít nhất 1 "edge case".

### 8.5. [SECURITY & PRIVACY]

- Không bao giờ log password, token, hoặc thông tin thẻ tín dụng.
- Không commit file .env lên Git.
- Không nhúng secret key (JWT secret, VNPay hash) trực tiếp trong code – bắt buộc dùng biến môi trường.

## 9. GHI CHÚ BỔ SUNG (Living Document)

File này được cập nhật định kỳ. Mọi thay đổi về kiến trúc, quy trình hoặc business rules phải được phản ánh lại đây.

Phiên bản hiện tại: 1.0.0

Lần cập nhật cuối: 2025-03-10 (theo thời gian của request)

Lưu ý: Agent hãy luôn tham chiếu đến tài liệu này trước khi trả lời bất kỳ câu hỏi hoặc yêu cầu code nào liên quan đến dự án Torano.