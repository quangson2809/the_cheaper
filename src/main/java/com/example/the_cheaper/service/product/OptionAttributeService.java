package com.example.the_cheaper.service.product;

import com.example.the_cheaper.dto.response.user.UserOptionAttributeResponse;
import com.example.the_cheaper.dto.response.user.UserOptionValueResponse;
import com.example.the_cheaper.entity.OptionAttributeEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserOptionAttributeMapper;
import com.example.the_cheaper.repository.OptionAttributeRepository;
import com.example.the_cheaper.repository.OptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service phía User: chỉ cung cấp các API đọc (GET) thuộc tính và values.
 * Không yêu cầu phân quyền (public endpoint để hỗ trợ tính năng lọc sản phẩm).
 */
@Service
@RequiredArgsConstructor
public class OptionAttributeService {

    private final OptionAttributeRepository optionAttributeRepository;
    private final OptionValueRepository optionValueRepository;
    private final UserOptionAttributeMapper userOptionAttributeMapper;

    /**
     * Lấy danh sách tất cả thuộc tính (kèm các values của từng thuộc tính).
     * Dùng cho trang lọc sản phẩm phía client.
     */
    @Transactional(readOnly = true)
    public List<UserOptionAttributeResponse> listOptionAttributes() {
        return optionAttributeRepository.findAll().stream()
                .map(userOptionAttributeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách values của một thuộc tính cụ thể theo attributeId.
     */
    @Transactional(readOnly = true)
    public List<UserOptionValueResponse> listValuesByAttribute(Long attributeId) {
        OptionAttributeEntity attribute = optionAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy thuộc tính với id: " + attributeId));
        return optionValueRepository.findByOptionAttribute(attribute).stream()
                .map(userOptionAttributeMapper::toValueResponse)
                .collect(Collectors.toList());
    }
}
