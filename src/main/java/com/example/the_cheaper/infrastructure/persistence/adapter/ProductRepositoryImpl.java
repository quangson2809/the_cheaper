package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Product;
import com.example.the_cheaper.domain.repository.ProductRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.ProductEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.ProductPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaProductRepository;
import org.hibernate.annotations.SQLDelete;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    public ProductRepositoryImpl(
            JpaProductRepository jpaProductRepository,
            ProductPersistenceMapper productPersistenceMapper
    ) {
        this.jpaProductRepository = jpaProductRepository;
        this.productPersistenceMapper = productPersistenceMapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = productPersistenceMapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(productEntity);
        return productPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id)
                .map(productPersistenceMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(productPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaProductRepository.deleteById(id);
    }

    @Override
    public void delete(Product product) {

        jpaProductRepository.delete(productPersistenceMapper.toEntity(product));
    }

    @Override
    public boolean existsByName(String name) {

        return jpaProductRepository.existsByName(name);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaProductRepository.existsById(id);
    }

}
