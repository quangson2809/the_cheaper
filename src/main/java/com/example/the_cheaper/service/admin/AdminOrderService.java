package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminOrderFilterRequest;
import com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminOrderDetailResponse;
import com.example.the_cheaper.dto.response.admin.AdminOrderOverviewResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.exception.NotImplementedException;

import com.example.the_cheaper.mapper.admin.AdminOrderMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminOrderService {
    private final OrderRepository adminOrderRepository;
    private final AccountRepository accountRepository;
    private final AdminOrderMapper adminOrderMapper;
    private final AdminProtectedAccess adminProtectedAccess;

    public AdminOrderService(OrderRepository adminOrderRepository,
                             AccountRepository accountRepository,
                             AdminOrderMapper adminOrderMapper,
                             AdminProtectedAccess adminProtectedAccess) {
        this.adminOrderRepository = adminOrderRepository;
        this.accountRepository = accountRepository;
        this.adminOrderMapper = adminOrderMapper;
        this.adminProtectedAccess = adminProtectedAccess;
    }

    @Transactional(readOnly = true)
    public Page<AdminOrderOverviewResponse> listOrders(AccountEntity currentUser, AdminOrderFilterRequest request) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<OrderEntity> orderEntities = adminOrderRepository.findByAdminFilter(request.getStatus(), PageRequest.of(request.getPage() - 1, request.getLimit()));
        return orderEntities.map(adminOrderMapper::toOverviewResponse);
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailResponse getOrderDetail(AccountEntity currentUser,Long orderId) {
        adminProtectedAccess.adminAccess(currentUser);
        return adminOrderRepository.findById(orderId)
                .map(adminOrderMapper::toDetailResponse)
                .orElseThrow(() -> new NotImplementedException("Đơn hàng không tồn tại"));
    }

    @Transactional
    public AdminOrderOverviewResponse updateOrderStatus(Long orderId, AdminOrderStatusUpdateRequest request) {
        throw new NotImplementedException("Chức năng cập nhật trạng thái đơn hàng chưa được triển khai");
    }
}


