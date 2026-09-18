package com.example.the_cheaper.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Canonical runtime catalog. Seeding definitions must never imply staff grants. */
public final class PermissionCatalog {
    private PermissionCatalog() {}

    public record Definition(String code, String name, String description) {}

    public static final List<Definition> DEFINITIONS = List.of(
            permission("DASHBOARD_READ", "Xem thống kê quản trị"),
            permission("ACCOUNT_READ", "Xem tài khoản"),
            permission("ACCOUNT_CREATE", "Tạo tài khoản quản trị"),
            permission("ACCOUNT_DELETE", "Xóa tài khoản ngừng hoạt động"),
            permission("ACCOUNT_ROLE_READ", "Xem role của tài khoản"),
            permission("ACCOUNT_ROLE_UPDATE", "Cập nhật liên kết role của tài khoản"),
            permission("ACCOUNT_STATUS_UPDATE", "Cập nhật trạng thái tài khoản"),
            permission("ROLE_READ", "Xem role"),
            permission("ROLE_CREATE", "Tạo role"),
            permission("ROLE_UPDATE", "Cập nhật role"),
            permission("ROLE_DELETE", "Xóa role"),
            permission("ROLE_PERMISSION_READ", "Xem quyền của role"),
            permission("ROLE_PERMISSION_UPDATE", "Thay thế quyền của role"),
            permission("ROLE_PERMISSION_GRANT", "Cấp quyền cho role"),
            permission("ROLE_PERMISSION_REVOKE", "Thu hồi quyền của role"),
            permission("PERMISSION_READ", "Xem permission"),
            permission("PERMISSION_CREATE", "Tạo permission"),
            permission("PERMISSION_UPDATE", "Cập nhật permission"),
            permission("PERMISSION_DELETE", "Xóa permission"),
            permission("ORDER_READ", "Xem toàn bộ đơn hàng"),
            permission("ORDER_CONFIRM", "Xác nhận đơn hàng và giao hàng"),
            permission("ORDER_CANCEL", "Hủy đơn hàng"),
            permission("ORDER_DELIVERY_UPDATE", "Cập nhật giao hàng"),
            permission("ORDER_PAYMENT_COLLECT", "Ghi nhận thu tiền COD (API chưa triển khai)"),
            permission("USER_ORDER_READ", "Xem đơn hàng của mình"),
            permission("USER_ORDER_CREATE", "Tạo đơn hàng của mình"),
            permission("USER_ORDER_CANCEL", "Hủy đơn hàng của mình"),
            legacy("ORDER_UPDATE"), legacy("ACCOUNT_UPDATE"),
            legacy("ACCOUNT_ASSIGN_ROLE"), legacy("ROLE_ASSIGN_PERMISSION")
    );

    public static final Set<String> CODES = DEFINITIONS.stream()
            .map(Definition::code).collect(Collectors.toUnmodifiableSet());

    private static Definition permission(String code, String name) {
        return new Definition(code, name, name);
    }

    private static Definition legacy(String code) {
        return new Definition(code, code + " (legacy)",
                "Không cấp quyền runtime; chuyển đổi thủ công theo docs/rbac/BACKEND_RBAC_SYNC.md");
    }
}
