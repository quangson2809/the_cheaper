package com.example.the_cheaper.catalog.service;

import com.example.the_cheaper.catalog.dto.response.user.UserCategoryResponse;
import com.example.the_cheaper.catalog.mapper.user.UserCategoryMapper;
import com.example.the_cheaper.catalog.repository.CategoryRepository;
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
        return categoryRepository.findByStatus().stream()
                .map(userCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}
