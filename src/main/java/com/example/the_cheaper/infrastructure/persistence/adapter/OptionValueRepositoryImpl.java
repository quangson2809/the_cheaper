package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.OptionValue;
import com.example.the_cheaper.domain.repository.OptionValueRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.OptionValueEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.OptionValuePersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaOptionValueRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OptionValueRepositoryImpl implements OptionValueRepository {

    private final JpaOptionValueRepository jpaOptionValueRepository;
    private final OptionValuePersistenceMapper optionValuePersistenceMapper;

    public OptionValueRepositoryImpl(
            JpaOptionValueRepository jpaOptionValueRepository,
            OptionValuePersistenceMapper optionValuePersistenceMapper
    ) {
        this.jpaOptionValueRepository = jpaOptionValueRepository;
        this.optionValuePersistenceMapper = optionValuePersistenceMapper;
    }

    @Override
    public OptionValue save(OptionValue optionValue) {
        OptionValueEntity optionValueEntity = optionValuePersistenceMapper.toEntity(optionValue);
        OptionValueEntity savedEntity = jpaOptionValueRepository.save(optionValueEntity);
        return optionValuePersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<OptionValue> findById(Long id) {
        return jpaOptionValueRepository.findById(id)
                .map(optionValuePersistenceMapper::toDomain);
    }

    @Override
    public List<OptionValue> findAll() {
        return jpaOptionValueRepository.findAll().stream()
                .map(optionValuePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaOptionValueRepository.deleteById(id);
    }

    @Override
    public void delete(OptionValue optionValue) {
        jpaOptionValueRepository.delete(optionValuePersistenceMapper.toEntity(optionValue));
    }

}
