package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminRoleCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminRoleUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.service.authorization.SystemRoleGuard;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminRoleMapper;
import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AdminRoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<AdminRoleResponse> listRoles(AccountEntity currentUser) {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminRoleResponse getRole(Long id, AccountEntity currentUser) {
        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role với id: " + id));
    }

    @Transactional
    public AdminRoleResponse createRole(AdminRoleCreateRequest request, AccountEntity currentUser) {
        validateUniqueName(request.getName(), null);
        rejectReservedName(request.getName());

        RoleEntity entity = roleMapper.toEntity(request);
        return roleMapper.toResponse(roleRepository.save(entity));
    }

    @Transactional
    public AdminRoleResponse updateRole(Long id,
                                        AdminRoleUpdateRequest request,
                                        AccountEntity currentUser) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role với id: " + id));

        validateUniqueName(request.getName(), id);
        if (SystemRoleGuard.isSystemRole(entity.getName())) {
            if (!entity.getName().equals(request.getName())) {
                throw new InvalidInputException("Không thể đổi tên system role");
            }
        } else {
            rejectReservedName(request.getName());
        }
        roleMapper.updateEntityFromRequest(request, entity);

        return roleMapper.toResponse(roleRepository.save(entity));
    }

    @Transactional
    public void deleteRole(Long id, AccountEntity currentUser) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role với id: " + id));
        if (SystemRoleGuard.isSystemRole(entity.getName())) {
            throw new InvalidInputException("Không thể xóa system role");
        }

        if (accountRoleRepository.existsByRoleId(id)) {
            throw new ResourceAlreadyExistsException(
                    "Không thể xóa role vì role đang được gán cho account");
        }

        if (rolePermissionRepository.existsByRoleId(id)) {
            throw new ResourceAlreadyExistsException(
                    "Không thể xóa role vì role đang được gán permission");
        }

        roleRepository.deleteById(id);
    }

    private void validateUniqueName(String name, Long currentId) {
        boolean exists = currentId == null
                ? roleRepository.existsByName(name)
                : roleRepository.findByName(name)
                        .map(role -> !role.getId().equals(currentId))
                        .orElse(false);

        if (exists) {
            throw new ResourceAlreadyExistsException(
                    "Role '" + name + "' đã tồn tại");
        }
    }

    private void rejectReservedName(String name) {
        if (name != null && SystemRoleGuard.isSystemRole(name.trim())) {
            throw new InvalidInputException("Tên role được dành riêng cho hệ thống");
        }
    }
}
