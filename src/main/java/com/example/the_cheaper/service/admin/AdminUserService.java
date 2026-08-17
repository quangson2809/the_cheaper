package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminCreateAdminRequest;
import com.example.the_cheaper.dto.request.admin.AdminUserFilterRequest;
import com.example.the_cheaper.dto.request.admin.AssignAccountRoleRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.dto.response.admin.AdminAccountRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
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
                PageRequest.of(request.getPage() - 1, request.getLimit()));
        return accountEntities.map(adminAccountMapper::toResponse);
    }

    @Transactional
    public Page<AdminAccountResponse> searchAccountByPhone(String phone, AccountEntity currentUser, int page, int limit) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<AccountEntity> accountEntities = accountRepository.findActiveAccountByPhoneContainingIgnoreCase(
                phone, PageRequest.of(page - 1, limit));
        return accountEntities.map(adminAccountMapper::toResponse);
    }

    @Transactional
    public AdminAccountResponse createAdminAccount(AccountEntity currentUser, AdminCreateAdminRequest request) {
        adminProtectedAccess.adminAccess(currentUser);

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email đã được sử dụng");
        }

        RoleEntity adminRole = roleRepository.findByName("ADMIN")
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

    @Transactional(readOnly = true)
    public AdminAccountRoleResponse getAccountRole(AccountEntity currentUser, Long accountId) {
        adminProtectedAccess.adminAccess(currentUser);
        AccountEntity account = getAccount(accountId);
        return toRoleResponse(account);
    }

    @Transactional
    public AdminAccountRoleResponse assignAccountRole(
            AccountEntity currentUser,
            Long accountId,
            AssignAccountRoleRequest request) {
        adminProtectedAccess.adminAccess(currentUser);

        AccountEntity account = getAccount(accountId);
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role với id: " + request.getRoleId()));

        if (account.getRole() != null && account.getRole().getId().equals(role.getId())) {
            return toRoleResponse(account);
        }

        account.assignRole(role);
        accountRepository.save(account);
        return toRoleResponse(account);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        AccountEntity account = getAccount(accountId);
        if (account.getStatus() != 0) {
            throw new RuntimeException("Chỉ có thể xóa các tài khoản có trạng thái là 0 (Ngừng hoạt động)");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public AdminAccountResponse updateAccountStatus(AccountEntity currentUser, Long accountId, int status) {
        adminProtectedAccess.adminAccess(currentUser);
        AccountEntity account = getAccount(accountId);
        account.setStatus(status);
        accountRepository.save(account);
        return adminAccountMapper.toResponse(account);
    }

    private AccountEntity getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với id: " + accountId));
    }

    private AdminAccountRoleResponse toRoleResponse(AccountEntity account) {
        RoleEntity role = account.getRole();
        return AdminAccountRoleResponse.builder()
                .accountId(account.getId())
                .roleId(role != null ? role.getId() : null)
                .roleName(role != null ? role.getName() : null)
                .roleDescription(role != null ? role.getDescription() : null)
                .build();
    }
}
