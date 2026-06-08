package com.example.the_cheaper.service.product;

import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.dto.response.user.UserCategoryResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.CategoryEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminCategoryMapper;
import com.example.the_cheaper.mapper.user.UserCategoryMapper;
import com.example.the_cheaper.repository.CategoryRepository;
import com.example.the_cheaper.service.admin.AdminProtectedAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserCategoryMapper userCategoryMapper;

    @Transactional(readOnly = true)
    public List<UserCategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(userCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

}


