package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.entity.BrandEntity;
import com.example.the_cheaper.entity.CategoryEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminCategoryMapper;
import com.example.the_cheaper.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.the_cheaper.entity.AccountEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final AdminCategoryMapper adminCategoryMapper;
    private final AdminProtectedAccess adminProtectedAccess;

    @Transactional(readOnly = true)
    public List<AdminCategoryResponse> listCategories(AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        return categoryRepository.findAll().stream()
                .map(adminCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<AdminCategoryResponse> searchCategories(String name, AccountEntity currentUser, int page, int limit) {
        adminProtectedAccess.adminAccess(currentUser);
        Page<CategoryEntity> categoryEntities = categoryRepository.findCategoryByNameContainingIgnoreCase(
                name,
                PageRequest.of(page  - 1, limit)
        );

        return categoryEntities.map(adminCategoryMapper::toResponse);
    }

    @Transactional
    public AdminCategoryResponse createCategory(AdminCategoryRequest request, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Danh mục '" + request.getName() + "' đã tồn tại");
        }
        CategoryEntity entity = adminCategoryMapper.toEntity(request);
        if(request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        else {
            entity.setStatus(1);
        }
        return adminCategoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public AdminCategoryResponse getCategoryDetail(Long id, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));
        return adminCategoryMapper.toResponse(entity);
    }

    @Transactional
    public AdminCategoryResponse updateCategory(Long id, AdminCategoryRequest request, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));

        if (categoryRepository.existsByName(request.getName()) && !entity.getName().equals(request.getName())) {
            throw new ResourceAlreadyExistsException("Danh mục '" + request.getName() + "' đã tồn tại");
        }
        entity.setName(request.getName());
        if(request.getStatus()!=null){
            entity.setStatus(request.getStatus());
        }

        return adminCategoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Transactional
    public void deleteCategory(Long id, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void updateCategoryStatus(Long id,int status, AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));

        category.setStatus(status);
        categoryRepository.save(category);
    }

}


