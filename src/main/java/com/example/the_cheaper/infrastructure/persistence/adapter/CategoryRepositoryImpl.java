package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Category;
import com.example.the_cheaper.domain.repository.CategoryRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.CategoryEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    public CategoryRepositoryImpl(
            JpaCategoryRepository jpaCategoryRepository,
            CategoryPersistenceMapper categoryPersistenceMapper
    ) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.categoryPersistenceMapper = categoryPersistenceMapper;
    }

    @Override
    public Category save(Category category) {
        CategoryEntity categoryEntity = categoryPersistenceMapper.toEntity(category);
        CategoryEntity savedEntity = jpaCategoryRepository.save(categoryEntity);
        return categoryPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaCategoryRepository.findById(id)
                .map(categoryPersistenceMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll().stream()
                .map(categoryPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaCategoryRepository.deleteById(id);
    }

    @Override
    public void delete(Category category) {
        jpaCategoryRepository.delete(categoryPersistenceMapper.toEntity(category));
    }

    @Override
    public boolean existsByName(String name) {
        return jpaCategoryRepository.existsByName(name);
    }

}
