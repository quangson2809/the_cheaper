package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Brand;
import com.example.the_cheaper.domain.repository.BrandRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.BrandEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.BrandPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaBrandRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BrandRepositoryImpl implements BrandRepository {

    private final JpaBrandRepository jpaBrandRepository;
    private final BrandPersistenceMapper brandPersistenceMapper;

    public BrandRepositoryImpl(
            JpaBrandRepository jpaBrandRepository,
            BrandPersistenceMapper brandPersistenceMapper
    ) {
        this.jpaBrandRepository = jpaBrandRepository;
        this.brandPersistenceMapper = brandPersistenceMapper;
    }

    @Override
    public Brand save(Brand brand) {
        BrandEntity brandEntity = brandPersistenceMapper.toEntity(brand);
        BrandEntity savedEntity = jpaBrandRepository.save(brandEntity);
        return brandPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return jpaBrandRepository.findById(id)
                .map(brandPersistenceMapper::toDomain);
    }

    @Override
    public List<Brand> findAll() {
        return jpaBrandRepository.findAll().stream()
                .map(brandPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaBrandRepository.deleteById(id);
    }

    @Override
    public void delete(Brand brand) {
        jpaBrandRepository.delete(brandPersistenceMapper.toEntity(brand));
    }

    @Override
    public boolean existsByName(String name) {
        return jpaBrandRepository.existsByName(name);
    }

}
