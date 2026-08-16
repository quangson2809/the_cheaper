package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminOrderFilterRequest;
import com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.dto.response.admin.AdminOrderDetailResponse;
import com.example.the_cheaper.dto.response.admin.AdminOrderOverviewResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.BrandEntity;
import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.exception.NotImplementedException;

import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminOrderMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderRepository adminOrderRepository;
    private final AccountRepository accountRepository;
    private final AdminOrderMapper adminOrderMapper;
    private final AdminProtectedAccess adminProtectedAccess;

    @Transactional(readOnly = true)
    public Page<AdminOrderOverviewResponse> getListOrders(AccountEntity currentUser, AdminOrderFilterRequest request) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<OrderEntity> orderEntities = adminOrderRepository.findByAdminFilter(request.getStatus(), PageRequest.of(request.getPage() - 1, request.getLimit()));
        return orderEntities.map(adminOrderMapper::toOverviewResponse);
    }

    @Transactional
    public Page<AdminOrderOverviewResponse> searchOrders(Long id, AccountEntity currentUser, int page, int limit) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<OrderEntity> orderEntities = adminOrderRepository.findOrderByIdContainingIgnoreCase(id,
                PageRequest.of(page  - 1, limit));

        return orderEntities.map(adminOrderMapper::toOverviewResponse);
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailResponse getOrderDetail(AccountEntity currentUser,Long orderId) {
        adminProtectedAccess.adminAccess(currentUser);
        return adminOrderRepository.findById(orderId)
                .map(adminOrderMapper::toDetailResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
    }

    @Transactional
    public AdminOrderOverviewResponse updateOrderStatus(AccountEntity currentUser,Long orderId, AdminOrderStatusUpdateRequest request) {
        adminProtectedAccess.adminAccess(currentUser);

        OrderEntity orderEntity = adminOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotImplementedException("Đơn hàng không tồn tại"));

        setStatus(orderEntity,request.getStatus());

        adminOrderRepository.save(orderEntity);

        return adminOrderMapper.toOverviewResponse(orderEntity);
    }

    public void setStatus(OrderEntity order, OrderStatus status) {
       switch (order.getStatus()) {
            case PENDING:
                if(status.equals(OrderStatus.PROCESSING) || status.equals(OrderStatus.CANCELED)) {
                    //gán trạng thái
                    order.setStatus(status);
                } else {
                    throw new NotImplementedException("Trạng thái đơn hàng không hợp lệ");
                }
                break;
           case PROCESSING:
               if(status.equals(OrderStatus.SHIPPING) || status.equals(OrderStatus.CANCELED)) {
                   //gán trạng thái
                   order.setStatus(status);
               } else {
                   throw new NotImplementedException("Trạng thái đơn hàng không hợp lệ");
               }
               break;
           case SHIPPING:
               if (status.equals(OrderStatus.DELIVERED)) {
                   //ktra thanh toán
                   checkPaid(order);
                   //gán trạng thái
                   order.setStatus(status);
               } else {
                   throw new NotImplementedException("Trạng thái đơn hàng không hợp lệ");
               }
               break;
           case DELIVERED, CANCELED:
                throw new NotImplementedException("Trạng thái đơn hàng không hợp lệ");
           default:
                throw new NotImplementedException("Trạng thái đơn hàng không hợp lệ: " + status);
        }
    }

    public void checkPaid(OrderEntity order) {
        if(!order.isPaid()) {
            throw new NotImplementedException("Đơn hàng chưa được thanh toán");
        }
    }
}


