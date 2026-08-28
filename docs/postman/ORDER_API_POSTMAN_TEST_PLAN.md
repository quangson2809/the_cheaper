# The Cheaper — Order API Postman Test Plan

## 1. Mục tiêu

Tài liệu này dùng để kiểm thử thủ công module Order trên branch `feature/order-rbac-refactor`, tập trung vào 4 invariant:

1. Authentication cấp JWT có permission từ RBAC mới.
2. User chỉ đọc/hủy Order của chính mình.
3. Admin đọc/cập nhật được Order toàn hệ thống, không có owner constraint.
4. Permission boundary của User và Admin không bị lẫn.

> Đây là tài liệu test API bằng Postman, không thay thế unit/integration test của backend.

---

## 2. Chuẩn bị backend

### 2.1. Branch

```text
feature/order-rbac-refactor
```

### 2.2. Chạy backend

Mặc định Spring Boot chạy ở:

```text
http://localhost:8080
```

Environment variable trong Postman:

```text
baseUrl = http://localhost:8080
```

Nếu backend chạy port khác, chỉ cần đổi `baseUrl`.

### 2.3. Database seed

`DataSeeder` tạo sẵn:

```text
USER 1: an.nguyen@gmail.com
USER 2: binh.tran@gmail.com
ADMIN : admin@gmail.com
Password cho các account seed: 123456
```

Dùng **USER 1** và **USER 2** để kiểm tra ownership.

> Chỉ dùng các account này trong môi trường local/dev. Không dùng credential seed trong production.

---

## 3. Postman Environment

Tạo environment `TheCheaper - Local` với các biến:

| Variable | Initial value | Ý nghĩa |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | Base URL |
| `userEmail` | `an.nguyen@gmail.com` | User A |
| `userPassword` | `123456` | User A password |
| `user2Email` | `binh.tran@gmail.com` | User B |
| `user2Password` | `123456` | User B password |
| `adminEmail` | `admin@gmail.com` | Admin |
| `adminPassword` | `123456` | Admin password |
| `userToken` | empty | JWT User A |
| `user2Token` | empty | JWT User B |
| `adminToken` | empty | JWT Admin |
| `userOrderId` | empty | Order thuộc User A |
| `user2OrderId` | empty | Order thuộc User B |
| `anyOrderId` | empty | Order dùng để test admin |

Không đưa JWT thật vào collection JSON. Token được lưu tự động sau request login.

---

## 4. Header chuẩn

### User/Admin protected API

```http
Authorization: Bearer {{userToken}}
Content-Type: application/json
```

Hoặc thay bằng `{{user2Token}}` / `{{adminToken}}` tùy test.

### Public login

Không cần Authorization.

---

# 5. Test flow tổng thể

Chạy theo thứ tự:

```text
01 Login User A
      ↓
02 Login User B
      ↓
03 Login Admin
      ↓
04 User A - Create Order
      ↓
05 User A - Get My Orders
      ↓
06 User A - Get Own Order
      ↓
07 User A - Get User B Order → MUST NOT ACCESS
      ↓
08 User A - Cancel Own Order
      ↓
09 Admin - Get All Orders
      ↓
10 Admin - Get Any Order
      ↓
11 Admin - Update Order Status
      ↓
12 User tries Admin API → MUST 403
```

---

# 6. Authentication tests

## 6.1. Login User A

```http
POST {{baseUrl}}/api/auth/login
Content-Type: application/json
```

Body:

```json
{
  "email": "{{userEmail}}",
  "password": "{{userPassword}}"
}
```

Expected:

```text
HTTP 200
```

Response phải có:

```json
{
  "data": {
    "accessToken": "...",
    "refreshToken": "..."
  }
}
```

Lưu `accessToken` vào `{{userToken}}` bằng Postman test script:

```javascript
const json = pm.response.json();
pm.environment.set("userToken", json.data.accessToken);
```

---

## 6.2. Login User B

```http
POST {{baseUrl}}/api/auth/login
```

Body:

```json
{
  "email": "{{user2Email}}",
  "password": "{{user2Password}}"
}
```

Script:

```javascript
const json = pm.response.json();
pm.environment.set("user2Token", json.data.accessToken);
```

Expected: `200`.

---

## 6.3. Login Admin

```http
POST {{baseUrl}}/api/auth/login
```

Body:

```json
{
  "email": "{{adminEmail}}",
  "password": "{{adminPassword}}"
}
```

Script:

```javascript
const json = pm.response.json();
pm.environment.set("adminToken", json.data.accessToken);
```

Expected: `200`.

---

# 7. User Order API

## 7.1. Create Order

```http
POST {{baseUrl}}/api/orders
Authorization: Bearer {{userToken}}
Content-Type: application/json
```

Body:

```json
{
  "paymentMethodId": 1,
  "receiver": "Nguyễn Văn An",
  "location": "Hà Nội",
  "phone": "0901234567"
}
```

Expected:

```text
HTTP 201
```

Sau khi response thành công, lấy `id` của order và lưu vào:

```text
{{userOrderId}}
```

> `paymentMethodId` phải tồn tại trong database hiện tại. Seeder hiện tạo `COD`, `MOMO`, `VNPAY`.

### Security check

Client không truyền `accountId`.

Owner của order phải được backend lấy từ authenticated account.

---

## 7.2. Get My Orders

```http
GET {{baseUrl}}/api/orders?page=1&limit=10
Authorization: Bearer {{userToken}}
```

Permission yêu cầu:

```text
USER_ORDER_READ
```

Expected:

```text
HTTP 200
```

Mọi order trong result phải thuộc User A.

---

## 7.3. Get Own Order

```http
GET {{baseUrl}}/api/orders/{{userOrderId}}
Authorization: Bearer {{userToken}}
```

Expected:

```text
HTTP 200
```

Permission:

```text
USER_ORDER_READ
```

Ownership:

```text
order.account.id == authenticatedUser.id
```

---

## 7.4. Get Other User's Order — IDOR Test

Đầu tiên phải có `{{user2OrderId}}` thuộc User B.

```http
GET {{baseUrl}}/api/orders/{{user2OrderId}}
Authorization: Bearer {{userToken}}
```

Expected:

```text
HTTP 404
```

Không được trả dữ liệu Order của User B.

Không được fallback sang:

```java
findById(orderId)
```

cho User API.

---

## 7.5. Cancel Own Order

```http
POST {{baseUrl}}/api/orders/{{userOrderId}}/cancel
Authorization: Bearer {{userToken}}
```

Expected:

```text
HTTP 200
```

với điều kiện Order đang ở trạng thái cho phép cancel và payment method phù hợp với business rule hiện tại.

Permission:

```text
USER_ORDER_CANCEL
```

---

## 7.6. Cancel Other User's Order — IDOR Test

```http
POST {{baseUrl}}/api/orders/{{user2OrderId}}/cancel
Authorization: Bearer {{userToken}}
```

Expected:

```text
HTTP 404
```

Không được đổi status Order của User B.

---

# 8. Admin Order API

## 8.1. Get All Orders

```http
GET {{baseUrl}}/api/admin/orders?page=1&limit=10
Authorization: Bearer {{adminToken}}
```

Expected:

```text
HTTP 200
```

Permission:

```text
ORDER_READ
```

Quan trọng:

```text
Không truyền accountId.
Không filter theo current owner.
```

Admin phải nhìn được Order của nhiều account.

---

## 8.2. Get Any Order

```http
GET {{baseUrl}}/api/admin/orders/{{anyOrderId}}
Authorization: Bearer {{adminToken}}
```

Expected:

```text
HTTP 200
```

Repository lookup phải tương đương:

```java
findById(orderId)
```

Không phải:

```java
findByIdAndAccountId(orderId, accountId)
```

---

## 8.3. Admin Update Order Status

```http
PATCH {{baseUrl}}/api/admin/orders/{{anyOrderId}}/status
Authorization: Bearer {{adminToken}}
Content-Type: application/json
```

Body ví dụ:

```json
{
  "status": "PROCESSING"
}
```

Expected:

```text
HTTP 200
```

Permission:

```text
ORDER_UPDATE
```

Status transition vẫn phải hợp lệ theo state machine.

---

# 9. Permission isolation tests

## 9.1. User gọi Admin Order GET

```http
GET {{baseUrl}}/api/admin/orders
Authorization: Bearer {{userToken}}
```

Expected:

```text
HTTP 403
```

Lý do:

```text
USER có USER_ORDER_READ
USER không có ORDER_READ
```

---

## 9.2. User gọi Admin Order Detail

```http
GET {{baseUrl}}/api/admin/orders/{{anyOrderId}}
Authorization: Bearer {{userToken}}
```

Expected: `403`.

---

## 9.3. User gọi Admin Update

```http
PATCH {{baseUrl}}/api/admin/orders/{{anyOrderId}}/status
Authorization: Bearer {{userToken}}
```

Expected: `403`.

---

## 9.4. Anonymous gọi User Order

Không gửi Authorization:

```http
GET {{baseUrl}}/api/orders
```

Expected:

```text
HTTP 401
```

---

# 10. State machine tests

Các transition hợp lệ hiện tại:

```text
PENDING
 ├── PROCESSING
 └── CANCELED

PROCESSING
 ├── SHIPPING
 └── CANCELED

SHIPPING
 └── DELIVERED
```

Terminal:

```text
DELIVERED
CANCELED
REFUNDED
```

không được transition tiếp trong flow hiện tại.

## 10.1. Invalid transition

Ví dụ:

```json
{
  "status": "DELIVERED"
}
```

khi Order đang `PENDING`.

Expected:

```text
HTTP 400
```

## 10.2. Cancel delivered order

User hoặc API flow cố cancel Order đã `DELIVERED`.

Expected:

```text
HTTP 400
```

Không được đổi status.

## 10.3. Deliver unpaid order

Admin chuyển:

```text
SHIPPING → DELIVERED
```

khi `paymentStatus != 1`.

Expected:

```text
HTTP 400
```

---

# 11. Permission mutation regression test

Mục tiêu là xác nhận API Order thực sự dựa trên permission, không dựa vào tên role.

### Test

1. Login User A.
2. Gọi `GET /api/orders` → `200`.
3. Revoke `USER_ORDER_READ` khỏi role/user trong RBAC admin UI/API.
4. Login lại User A hoặc sử dụng auth lifecycle hiện tại để nhận authorities mới.
5. Gọi lại `GET /api/orders`.

Expected:

```text
HTTP 403
```

Thực hiện tương tự với:

```text
USER_ORDER_CANCEL
ORDER_READ
ORDER_UPDATE
```

---

# 12. Regression checklist

| # | Test | Expected |
|---|---|---|
| 1 | Login User A | 200 |
| 2 | Login User B | 200 |
| 3 | Login Admin | 200 |
| 4 | User create order | 201 |
| 5 | User get my orders | 200 |
| 6 | User get own order | 200 |
| 7 | User get other user's order | 404 |
| 8 | User cancel own order | 200 nếu business rule cho phép |
| 9 | User cancel other user's order | 404 |
| 10 | Admin get all orders | 200 |
| 11 | Admin get any order | 200 |
| 12 | Admin update status | 200 nếu transition hợp lệ |
| 13 | User call Admin GET | 403 |
| 14 | User call Admin UPDATE | 403 |
| 15 | Anonymous call User Order | 401 |
| 16 | Invalid status transition | 400 |
| 17 | Cancel terminal order | 400 |
| 18 | Deliver unpaid order | 400 |
| 19 | Revoke USER_ORDER_READ | 403 |
| 20 | Revoke ORDER_READ | 403 |

---

# 13. Kết quả cần đạt

Sau khi chạy toàn bộ collection, phải chứng minh được:

```text
                    RBAC
                     │
          ┌──────────┴──────────┐
          │                     │
        USER                  ADMIN
          │                     │
 USER_ORDER_*              ORDER_*
          │                     │
          ▼                     ▼
  owner-scoped queries     global queries
          │                     │
          └──────────┬──────────┘
                     ▼
                  Order
```

### Invariant bắt buộc

```text
USER:
  không thấy Order của account khác
  không cancel được Order của account khác
  không vào được /api/admin/orders

ADMIN:
  xem được Order của mọi account
  update được Order của mọi account
  không cần ownerId/accountId
```

Nếu một trong các assertion trên fail, không merge Order branch trước khi xác định nguyên nhân.
