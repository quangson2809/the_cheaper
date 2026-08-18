package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.AdminOptionAttributeRequest;
import com.example.the_cheaper.dto.request.admin.AdminOptionValueRequest;
import com.example.the_cheaper.dto.response.admin.AdminOptionAttributeResponse;
import com.example.the_cheaper.dto.response.admin.AdminOptionValueResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.OptionAttributeEntity;
import com.example.the_cheaper.entity.OptionValueEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminOptionAttributeMapper;
import com.example.the_cheaper.repository.OptionAttributeRepository;
import com.example.the_cheaper.repository.OptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOptionAttributeService {

    private final OptionAttributeRepository optionAttributeRepository;
    private final OptionValueRepository optionValueRepository;
    private final AdminOptionAttributeMapper adminOptionAttributeMapper;

    @Transactional(readOnly = true)
    public List<AdminOptionAttributeResponse> listOptionAttributes(AccountEntity currentUser) {
        return optionAttributeRepository.findAll().stream()
                .map(adminOptionAttributeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<AdminOptionAttributeResponse> searchOptionAttribute(String name, AccountEntity currentUser, int page, int limit) {
        Page<OptionAttributeEntity> optionAttributeEntities = optionAttributeRepository.findOptionAttributeByNameContainingIgnoreCase(name,
                PageRequest.of(page - 1, limit));

        return optionAttributeEntities.map(adminOptionAttributeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AdminOptionValueResponse> listValuesByAttribute(Long attributeId, AccountEntity currentUser) {
        OptionAttributeEntity attribute = optionAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + attributeId));
        return optionValueRepository.findByOptionAttribute(attribute).stream()
                .map(adminOptionAttributeMapper::toValueResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminOptionAttributeResponse getOptionAttributeDetail(Long id, AccountEntity currentUser) {
        OptionAttributeEntity entity = optionAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + id));
        return adminOptionAttributeMapper.toResponse(entity);
    }

    @Transactional
    public AdminOptionAttributeResponse createOptionAttribute(AdminOptionAttributeRequest request,
                                                               AccountEntity currentUser) {
        if (optionAttributeRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Thuộc tính '" + request.getName() + "' đã tồn tại");
        }

        OptionAttributeEntity attribute = new OptionAttributeEntity();
        attribute.setName(request.getName());

        createOptionValue(attribute, request.getValues());

        return adminOptionAttributeMapper.toResponse(optionAttributeRepository.save(attribute));
    }

    public void createOptionValue(OptionAttributeEntity attribute, List<AdminOptionValueRequest> request) {
        if (request != null && !request.isEmpty()) {
            List<OptionValueEntity> values = request.stream()
                    .map(valReq -> {
                        OptionValueEntity val = adminOptionAttributeMapper.toValueEntity(valReq);
                        val.setOptionAttribute(attribute);
                        return val;
                    })
                    .collect(Collectors.toList());
            attribute.setValues(values);
        }
    }

    @Transactional
    public AdminOptionAttributeResponse updateOptionAttribute(Long id,
                                                               AdminOptionAttributeRequest request,
                                                               AccountEntity currentUser) {
        OptionAttributeEntity entity = optionAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + id));

        processOptionValue(entity, request.getValues());
        processOptionAttribute(entity, request.getName());

        optionAttributeRepository.save(entity);

        return adminOptionAttributeMapper.toResponse(optionAttributeRepository.save(entity));
    }

    public void processOptionAttribute(OptionAttributeEntity entity, String newName) {
        if (newName != null) {
            entity.setName(newName);
        }
    }

    public void processOptionValue(OptionAttributeEntity entity, List<AdminOptionValueRequest> newValues) {
        if (newValues != null) {
            newValues.forEach(valReq -> {
                for (OptionValueEntity val : entity.getValues()) {
                    if (val.getId().equals(valReq.getId())) {
                        val.setValue(valReq.getValue());
                        break;
                    }
                }
            });
        }
    }

    @Transactional
    public AdminOptionValueResponse addValueToAttribute(Long attributeId,
                                                         AdminOptionValueRequest request,
                                                         AccountEntity currentUser) {
        OptionAttributeEntity attribute = optionAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + attributeId));

        if (optionValueRepository.existsByValueAndOptionAttribute(request.getValue(), attribute)) {
            throw new ResourceAlreadyExistsException(
                    "Giá trị '" + request.getValue() + "' đã tồn tại trong thuộc tính này");
        }

        OptionValueEntity newValue = adminOptionAttributeMapper.toValueEntity(request);
        newValue.setOptionAttribute(attribute);
        return adminOptionAttributeMapper.toValueResponse(optionValueRepository.save(newValue));
    }

    @Transactional
    public void deleteValue(Long attributeId, Long valueId, AccountEntity currentUser) {
        OptionAttributeEntity attribute = optionAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + attributeId));

        OptionValueEntity value = optionValueRepository.findById(valueId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giá trị với id: " + valueId));

        if (!value.getOptionAttribute().getId().equals(attribute.getId())) {
            throw new ResourceNotFoundException("Giá trị không thuộc về thuộc tính này");
        }

        optionValueRepository.delete(value);
    }

    @Transactional
    public void deleteOptionAttribute(Long id, AccountEntity currentUser) {
        OptionAttributeEntity entity = optionAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + id));
        if (!optionAttributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thuộc tính với id: " + id);
        }
        if (!canDelete(entity)) {
            optionAttributeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không thể xóa thuộc tính này vì nó đang được sử dụng bởi các sản phẩm");
        }
    }

    public boolean canDelete(OptionAttributeEntity entity) {
        return entity.getValues() == null || entity.getValues().isEmpty();
    }
}
