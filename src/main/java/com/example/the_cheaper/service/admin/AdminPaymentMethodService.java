package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminCreatePaymentMethodRequest;
import com.example.the_cheaper.dto.request.admin.AdminUpdatePaymentMethodRequest;
import com.example.the_cheaper.dto.response.common.PaymentMethodResponse;
import com.example.the_cheaper.entity.PaymentMethodEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminPaymentMethodMapper;
import com.example.the_cheaper.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.the_cheaper.entity.AccountEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final AdminPaymentMethodMapper adminPaymentMethodMapper;
    private final AdminProtectedAccess adminProtectedAccess;

    // ─── Lấy tất cả (admin) ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllPaymentMethods(AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        return paymentMethodRepository.findAll()
                .stream()
                .map(adminPaymentMethodMapper::toResponse)
                .toList();
    }

    // ─── Lấy chi tiết ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethod(Long id, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        PaymentMethodEntity entity = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phương thức thanh toán với id: " + id));
        return adminPaymentMethodMapper.toResponse(entity);
    }

    // ─── Tạo mới ─────────────────────────────────────────────────────────────────

    @Transactional
    public PaymentMethodResponse createPaymentMethod(AdminCreatePaymentMethodRequest request, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        String code = request.getCode().toUpperCase().trim();

        if (paymentMethodRepository.existsByCode(code)) {
            throw new ResourceAlreadyExistsException(
                    "Phương thức thanh toán với code '" + code + "' đã tồn tại");
        }

        PaymentMethodEntity entity = new PaymentMethodEntity();
        entity.setName(request.getName());
        entity.setCode(code);

        return adminPaymentMethodMapper.toResponse(paymentMethodRepository.save(entity));
    }

    // ─── Cập nhật ────────────────────────────────────────────────────────────────

    @Transactional
    public PaymentMethodResponse updatePaymentMethod(Long id, AdminUpdatePaymentMethodRequest request, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        PaymentMethodEntity entity = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phương thức thanh toán với id: " + id));

        if(request.getName() != null && !request.getName().isEmpty()) {
            entity.setName(request.getName());
        }
        if(request.getCode() != null && !request.getCode().isEmpty()) {
            entity.setCode(request.getCode());
        }
        if(request.getStatus() != null ) {
            entity.setStatus(request.getStatus());
        }

        return adminPaymentMethodMapper.toResponse(paymentMethodRepository.save(entity));
    }

    // ─── Xóa mềm (soft delete: active = false) ───────────────────────────────────

    @Transactional
    public void deletePaymentMethod(Long id, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        PaymentMethodEntity entity = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phương thức thanh toán với id: " + id));
        entity.setStatus(0);
        paymentMethodRepository.save(entity);
    }
}
