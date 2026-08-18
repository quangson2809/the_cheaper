package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminMaterialRequest;
import com.example.the_cheaper.dto.response.admin.AdminMaterialResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.MaterialEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminMaterialMapper;
import com.example.the_cheaper.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMaterialService {

    private final MaterialRepository materialRepository;
    private final AdminMaterialMapper adminMaterialMapper;

    @Transactional(readOnly = true)
    public List<AdminMaterialResponse> listMaterials(AccountEntity currentUser) {
        return materialRepository.findAll().stream()
                .map(adminMaterialMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<AdminMaterialResponse> searchMaterials(String name, AccountEntity currentUser, int page, int limit) {
        Page<MaterialEntity> materialEntities = materialRepository.findMaterialByNameContainingIgnoreCase(name,
                PageRequest.of(page - 1, limit));

        return materialEntities.map(adminMaterialMapper::toResponse);
    }

    @Transactional
    public AdminMaterialResponse createMaterial(AdminMaterialRequest request, AccountEntity currentUser) {
        if (materialRepository.findByName(request.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Chất liệu '" + request.getName() + "' đã tồn tại");
        }
        MaterialEntity entity = adminMaterialMapper.toEntity(request);
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        } else {
            entity.setStatus(1);
        }

        return adminMaterialMapper.toResponse(materialRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public AdminMaterialResponse getMaterialDetail(Long id, AccountEntity currentUser) {
        MaterialEntity entity = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chất liệu với id: " + id));
        return adminMaterialMapper.toResponse(entity);
    }

    @Transactional
    public AdminMaterialResponse updateMaterial(Long id, AdminMaterialRequest request, AccountEntity currentUser) {
        MaterialEntity entity = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chất liệu với id: " + id));

        materialRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ResourceAlreadyExistsException("Chất liệu '" + request.getName() + "' đã tồn tại");
                    }
                });
        entity.setName(request.getName());

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        return adminMaterialMapper.toResponse(materialRepository.save(entity));
    }

    @Transactional
    public void deleteMaterial(Long id, AccountEntity currentUser) {
        if (!materialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy chất liệu với id: " + id);
        }
        materialRepository.deleteById(id);
    }

    @Transactional
    public void updateMaterialStatus(Long id, int status, AccountEntity currentUser) {
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chất liệu với id: " + id));

        material.setStatus(status);
        materialRepository.save(material);
    }
}
