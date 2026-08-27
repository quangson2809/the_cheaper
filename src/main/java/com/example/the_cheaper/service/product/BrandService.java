package com.example.the_cheaper.service.product;

import com.example.the_cheaper.dto.response.user.UserBrandResponse;
import com.example.the_cheaper.mapper.user.UserBrandMapper;
import com.example.the_cheaper.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final UserBrandMapper userBrandMapper;

    @Transactional(readOnly = true)
    public List<UserBrandResponse> listBrands() {
        return brandRepository.findByStatus().stream()
                .map(userBrandMapper::toResponse)
                .collect(Collectors.toList());
    }
}
