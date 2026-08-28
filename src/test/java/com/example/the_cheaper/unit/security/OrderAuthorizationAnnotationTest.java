package com.example.the_cheaper.unit.security;

import com.example.the_cheaper.controller.admin.AdminOrderController;
import com.example.the_cheaper.controller.user.UserOrderController;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderAuthorizationAnnotationTest {

    @Test
    void adminOrderEndpointsUseAdminOrderPermissions() throws Exception {
        assertEquals("hasAuthority('ORDER_READ')", preAuthorize(AdminOrderController.class, "getListOrders"));
        assertEquals("hasAuthority('ORDER_READ')", preAuthorize(AdminOrderController.class, "getOrderDetail", Long.class));
        assertEquals("hasAuthority('ORDER_UPDATE')", preAuthorize(AdminOrderController.class, "updateOrderStatus", Long.class,
                com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest.class));
    }

    @Test
    void userOrderEndpointsUseUserOrderPermissions() throws Exception {
        assertEquals("hasAuthority('USER_ORDER_CREATE')", preAuthorize(UserOrderController.class, "createOrder",
                com.example.the_cheaper.entity.AccountEntity.class,
                com.example.the_cheaper.dto.request.user.UserCreateOrderRequest.class));
        assertEquals("hasAuthority('USER_ORDER_READ')", preAuthorize(UserOrderController.class, "getMyOrders",
                com.example.the_cheaper.entity.AccountEntity.class, int.class, int.class));
        assertEquals("hasAuthority('USER_ORDER_READ')", preAuthorize(UserOrderController.class, "getOrderDetail",
                com.example.the_cheaper.entity.AccountEntity.class, Long.class));
        assertEquals("hasAuthority('USER_ORDER_CANCEL')", preAuthorize(UserOrderController.class, "cancelOrder",
                com.example.the_cheaper.entity.AccountEntity.class, Long.class));
    }

    private String preAuthorize(Class<?> controller, String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = controller.getDeclaredMethod(methodName, parameterTypes);
        return method.getAnnotation(PreAuthorize.class).value();
    }
}
