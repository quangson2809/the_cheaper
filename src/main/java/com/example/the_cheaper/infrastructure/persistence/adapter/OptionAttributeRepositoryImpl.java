package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.OptionAttribute;
import com.example.the_cheaper.domain.repository.OptionAttributeRepository;
import com.example.the_cheaper.infrastructure.persistence.mapper.OptionAttributePersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaOptionAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OptionAttributeRepositoryImpl implements OptionAttributeRepository {

    private final JpaOptionAttributeRepository jpaRepository;
    private final OptionAttributePersistenceMapper mapper;

    @Override
    public OptionAttribute save(OptionAttribute attribute) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(attribute)));
    }

    @Override
    public Optional<OptionAttribute> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
