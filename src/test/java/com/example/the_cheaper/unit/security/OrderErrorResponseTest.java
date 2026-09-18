package com.example.the_cheaper.unit.security;

import com.example.the_cheaper.controller.admin.AdminOrderController;
import com.example.the_cheaper.exception.GlobalExceptionHandler;
import com.example.the_cheaper.service.admin.AdminOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Tests the HTTP error mapping only; authorization is covered through real JWT integration tests. */
class OrderErrorResponseTest {
    @Test
    void staleUpdateReturns409WithApiResponse() throws Exception {
        AdminOrderService service = mock(AdminOrderService.class);
        when(service.updateOrderStatus(eq(1L), any()))
                .thenThrow(new OptimisticLockingFailureException("stale row"));
        var mvc = MockMvcBuilders.standaloneSetup(new AdminOrderController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(patch("/api/admin/orders/1/status").contentType("application/json")
                        .content("{\"status\":\"PROCESSING\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/api/admin/orders/1/status"));
    }
}
