package com.example.the_cheaper.service.authorization;

import com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest;
import com.example.the_cheaper.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Set;

/** Shared permission policy for HTTP controllers and Spring-managed services. */
@Component("orderAccess")
public class OrderAccess {
    private static final Set<String> STATUS_PERMISSIONS = Set.of(
            "ORDER_CONFIRM", "ORDER_CANCEL", "ORDER_DELIVERY_UPDATE");

    public boolean canUpdate(Authentication authentication, AdminOrderStatusUpdateRequest request) {
        if (!active(authentication)) return false;
        if (has(authentication, "ROLE_ADMIN")) return true;
        String permission = request == null || request.getStatus() == null ? null : switch (request.getStatus()) {
            case PROCESSING, DELIVERED -> "ORDER_CONFIRM";
            case CANCELED -> "ORDER_CANCEL";
            case SHIPPING -> "ORDER_DELIVERY_UPDATE";
            case PENDING, REFUNDED -> null;
        };
        // Authorized operators receive a validation error for unsupported targets.
        // Legacy ORDER_UPDATE and payment-only permissions never grant status access.
        return permission == null
                ? STATUS_PERMISSIONS.stream().anyMatch(code -> has(authentication, code))
                : has(authentication, permission);
    }

    public boolean canAccessOwn(Authentication authentication, Long accountId, String permission) {
        return active(authentication)
                && authentication.getPrincipal() instanceof CustomUserDetails details
                && accountId != null && accountId.equals(details.getAccount().getId())
                && (has(authentication, "ROLE_ADMIN") || has(authentication, permission));
    }

    private boolean active(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails details
                && details.isEnabled() && details.isAccountNonLocked();
    }

    private boolean has(Authentication authentication, String permission) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> permission.equals(authority.getAuthority()));
    }
}
