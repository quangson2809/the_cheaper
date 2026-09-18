# Việc tiếp nối và giới hạn nghiệm thu

Phạm vi được giữ ở Phase 5–6: API Order hiện có. Các mục dưới không được biến thành
ca skip rồi tính là đạt, cũng không được coi là đã làm chỉ vì permission/enum tồn tại.

## Nghiệp vụ hoãn — trạng thái: chưa bắt đầu triển khai luồng mới

| ID | Hiện tại | Cần chốt/triển khai và tiêu chí nghiệm thu sau này |
|---|---|---|
| BIZ-01 COD | COD tạo đơn chưa trả tiền; ORDER_PAYMENT_COLLECT chỉ trong catalog. Non-COD hiện bị đánh dấu đã trả tiền ngay | Chỉ chấp nhận phương thức đã hỗ trợ; API thu tiền có quyền riêng, số tiền/người thu/thời điểm, chống ghi trùng và audit; confirm không tự thu tiền; không giả trả tiền non-COD |
| BIZ-02 Giao thất bại | Không có enum/endpoint xử lý | Chốt trạng thái, lý do, lần giao lại và quyền ORDER_CONFIRM; transition, side effect kho/thanh toán và test retry rõ ràng |
| BIZ-03 Hoàn trả | Có enum REFUNDED nhưng không có luồng API | Chốt yêu cầu/duyệt/nhận hàng/hoàn tiền, quyền và phạm vi; không đồng nhất cancel với return; chống hoàn trùng |
| BIZ-04 Xuất kho | Checkout đang trừ stock | Chuyển sang xuất khi giao cần thiết kế giữ chỗ, giải phóng giữ chỗ, chống oversell; xử lý hủy trước/sau xuất khác nhau |
| BIZ-05 Hoàn kho | Hủy chỉ đổi status, không hoàn stock | Ghi nhận đã xuất/đã hoàn, hoàn đúng một lần và cùng transaction; migration đơn cũ, retry và cạnh tranh |
| BIZ-06 sold | Checkout tăng sold; hủy chưa giảm | Định nghĩa sold khi giao thành công và ảnh hưởng hoàn trả/giao thất bại; migration/backfill và thống kê nhất quán |

Không triển khai BIZ-04/BIZ-05/BIZ-06 độc lập bằng cách chỉ đổi một dòng stock/sold:
phải đánh giá dữ liệu cũ, trạng thái và thanh toán cùng nhau. Hồi quy baseline đã đạt
không chứng minh các mục tiêu nghiệp vụ mới này.

## Các điểm cần quyết định trước merge/triển khai rộng

Các phát hiện sau là rà soát source, không được tính là test động đã chứng minh
an toàn. Chúng có từ nền tảng hiện tại, không phải đã được xử lý bởi Phase 5.

| ID | Bằng chứng code / ảnh hưởng | Việc cần làm; trạng thái |
|---|---|---|
| RBAC-01 Dashboard | Đã thêm DASHBOARD_READ ở controller/service, test JWT bốn endpoint | Đã triển khai; chờ full suite có Docker. Xem [bản sửa](BACKEND_RBAC_SYNC.md) |
| RBAC-02 Role ADMIN | Chặn đổi tên/xóa ADMIN và USER, khóa/xóa admin hoạt động cuối cùng; kiểm tra đồng thời | Đã triển khai; chờ integration MySQL. Chính sách delegation/escalation tổng quát vẫn ngoài scope |
| RBAC-03 Catalog quản trị | Catalog dùng chung, giữ legacy inert, mapping xét duyệt thủ công, không cấp lại staff | Đã triển khai; chờ test restart MySQL. Xem [migration](BACKEND_RBAC_SYNC.md) |
| UI-01 Quyền hiệu lực | Đã có GET /api/auth/me/authorities cho union roles/permissions | Backend đã triển khai; frontend cần tích hợp, test JWT chờ môi trường Docker |
| OPS-01 Cấu hình triển khai | application.properties giữ cấu hình phát triển; DataSeeder mặc định có fixture tài khoản mẫu trên DB trống | Tách cấu hình/secret và bootstrap production, thay khóa và credential nếu từng dùng thật; chưa bắt đầu |
| OPS-02 Migration tổng quát | Hibernate update + reconciler; CI chỉ mô phỏng schema cũ và startup tuần tự | Diễn tập backup/restore và migration trên bản sao môi trường; chưa bắt đầu với DB thật |
| API-01 Retry/version client | Có optimistic locking DB, chưa có expected-version/If-Match/idempotency key | Chốt tránh ý định ghi từ UI cũ và checkout trùng; chưa bắt đầu |

PR bàn giao nên giữ **draft** cho tới khi reviewer quyết định phạm vi và cách xử lý
RBAC-01..03 cùng cấu hình rollout. Chấp nhận phần Order không tự là chấp nhận toàn
bộ API admin. Không tự merge hoặc tuyên bố sẵn sàng production từ kết quả 179 test.

## Cách tiếp nhận thay đổi yêu cầu

Mỗi mục mới cần ghi: yêu cầu và người quyết định, ảnh hưởng ma trận quyền, endpoint/
DTO/frontend, schema/dữ liệu cũ, trạng thái/kho/thanh toán, test bắt buộc, rollback.
Chốt ma trận trước thay API, chốt mapping quyền trước migration; cập nhật cùng lúc
hợp đồng, test và tài liệu. Theo dõi `chưa bắt đầu → đang làm → đã triển khai → đã
kiểm chứng`, kèm bằng chứng thực thi; không dùng số commit làm tỷ lệ hoàn thành.
