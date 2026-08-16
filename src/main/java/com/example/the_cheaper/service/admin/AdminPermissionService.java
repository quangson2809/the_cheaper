package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminPermissionCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminPermissionUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminPermissionResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminPermissionMapper;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AdminPermissionMapper permissionMapper;
    private final AdminProtectedAccess adminProtectedAccess;

    @Transactional
    public AdminPermissionResponse createPermission(
            AdminPermissionCreateRequest request,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        validateUnique(request.getName(), request.getCode(), null);

        PermissionEntity entity = permissionMapper.toEntity(request);
        return permissionMapper.toResponse(permissionRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public AdminPermissionResponse getPermission(Long id, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        return permissionRepository.findById(id)
                .map(permissionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy permission với id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<AdminPermissionResponse> listPermissions(
            int page,
            int size,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        Pageable pageable = PageRequest.of(normalizePage(page), normalizeSize(size));
        return permissionRepository.findAllByOrderByIdDesc(pageable)
                .map(permissionMapper::toResponse);
    }

    @Transactional
    public AdminPermissionResponse updatePermission(
            Long id,
            AdminPermissionUpdateRequest request,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);

        PermissionEntity entity = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy permission với id: " + id));

        validateUnique(request.getName(), request.getCode(), id);
        permissionMapper.updateEntityFromRequest(request, entity);

        return permissionMapper.toResponse(permissionRepository.save(entity));
    }

    @Transactional
    public void deletePermission(Long id, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);

        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy permission với id: " + id);
        }

        if (rolePermissionRepository.existsByPermissionId(id)) {
            throw new ResourceAlreadyExistsException(
                    "Không thể xóa permission vì permission đang được gán cho role");
        }

        permissionRepository.deleteById(id);
    }

    private void validateUnique(String name, String code, Long currentId) {
        boolean nameExists = currentId == null
                ? permissionRepository.existsByName(name)
                : permissionRepository.existsByNameAndIdNot(name, currentId);

        if (nameExists) {
            throw new ResourceAlreadyExistsException(
                    "Permission '" + name + "' đã tồn tại");
        }

        boolean codeExists = currentId == null
                ? permissionRepository.existsByCode(code)
                : permissionRepository.existsByCodeAndIdNot(code, currentId);

        if (codeExists) {
            throw new ResourceAlreadyExistsException(
                    "Permission code '" + code + "' đã tồn tại");
        }
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        return Math.min(Math.max(size, 1), 100);
    }
}
