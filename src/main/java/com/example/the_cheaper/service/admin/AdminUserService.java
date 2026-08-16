package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminUserFilterRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.dto.response.admin.AdminProductOverviewResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.NotImplementedException;

import java.util.List;

import com.example.the_cheaper.dto.request.admin.AdminCreateAdminRequest;
import com.example.the_cheaper.entity.Role;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminAccountMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final AdminProtectedAccess adminProtectedAccess;
    private final AccountRepository accountRepository;
    private final AdminAccountMapper adminAccountMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<AdminAccountResponse> listAccounts(AccountEntity currentUser, AdminUserFilterRequest request) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<AccountEntity> accountEntities = accountRepository.findAllBy(
                request.getStatus(),
                request.getRole(),
                PageRequest.of(request.getPage()  - 1, request.getLimit()));
        return accountEntities.map(adminAccountMapper::toResponse);
    }

    @Transactional
    public Page<AdminAccountResponse> searchAccountByPhone(String phone, AccountEntity currentUser, int page, int limit) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<AccountEntity> accountEntities = accountRepository.findActiveAccountByPhoneContainingIgnoreCase(phone,
                PageRequest.of(page  - 1, limit));

        return accountEntities.map(adminAccountMapper::toResponse);
    }

    @Transactional
    public AdminAccountResponse createAdminAccount(AccountEntity currentUser, AdminCreateAdminRequest request) {
        adminProtectedAccess.adminAccess(currentUser);
        
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email đã được sử dụng");
        }
        
        RoleEntity adminRole = roleRepository.findByName(Role.ADMIN.name())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role ADMIN"));

        AccountEntity newAdmin = AccountEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(adminRole)
                .status(1)
                .build();
                
        return adminAccountMapper.toResponse(accountRepository.save(newAdmin));
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        if (account.getStatus() != 0) {
            throw new RuntimeException("Chỉ có thể xóa các tài khoản có trạng thái là 0 (Ngừng hoạt động)");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public AdminAccountResponse updateAccountStatus(AccountEntity currentUser,Long accountId, int status) {
        adminProtectedAccess.adminAccess(currentUser);
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.setStatus(status);
        accountRepository.save(account);
        return adminAccountMapper.toResponse(account);
    }
}
