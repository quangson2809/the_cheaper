package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.BrandEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminBrandMapper;
import com.example.the_cheaper.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBrandService {

    private final BrandRepository brandRepository;
    private final AdminBrandMapper adminBrandMapper;

    @Transactional(readOnly = true)
    public List<AdminBrandResponse> listBrands(AccountEntity currentUser) {
        return brandRepository.findAll().stream()
                .map(adminBrandMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<AdminBrandResponse> searchBrands(String name, AccountEntity currentUser, int page, int limit) {
        Page<BrandEntity> brandEntities = brandRepository.findBrandByNameContainingIgnoreCase(name,
                PageRequest.of(page - 1, limit));

        return brandEntities.map(adminBrandMapper::toResponse);
    }

    @Transactional
    public AdminBrandResponse createBrand(AdminBrandRequest request, AccountEntity currentUser) {
        if (brandRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Thương hiệu '" + request.getName() + "' đã tồn tại");
        }
        BrandEntity entity = adminBrandMapper.toEntity(request);
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        } else {
            entity.setStatus(1);
        }
        return adminBrandMapper.toResponse(brandRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public AdminBrandResponse getBrandDetail(Long id, AccountEntity currentUser) {
        BrandEntity entity = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id: " + id));
        return adminBrandMapper.toResponse(entity);
    }

    @Transactional
    public AdminBrandResponse updateBrand(Long id, AdminBrandRequest request, AccountEntity currentUser) {
        BrandEntity entity = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id: " + id));

        if (brandRepository.existsByName(request.getName()) && !entity.getName().equals(request.getName())) {
            throw new ResourceAlreadyExistsException("Thương hiệu '" + request.getName() + "' đã tồn tại");
        }
        entity.setName(request.getName());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        return adminBrandMapper.toResponse(brandRepository.save(entity));
    }

    @Transactional
    public void deleteBrand(Long id, AccountEntity currentUser) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu với id: " + id);
        }
        brandRepository.deleteById(id);
    }

    @Transactional
    public void updateBrandStatus(Long id, int status, AccountEntity currentUser) {
        BrandEntity brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id: " + id));

        brand.setStatus(status);
        brandRepository.save(brand);
    }
}
