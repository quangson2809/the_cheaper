package com.example.the_cheaper.service.product;

import com.example.the_cheaper.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.dto.response.user.UserBrandResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.BrandEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminBrandMapper;
import com.example.the_cheaper.mapper.user.UserBrandMapper;
import com.example.the_cheaper.repository.BrandRepository;
import com.example.the_cheaper.service.admin.AdminProtectedAccess;
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
    public List<UserBrandResponse> listBrands( ) {
        return brandRepository.findByStatus().stream()
                .map(userBrandMapper::toResponse)
                .collect(Collectors.toList());
    }
}


