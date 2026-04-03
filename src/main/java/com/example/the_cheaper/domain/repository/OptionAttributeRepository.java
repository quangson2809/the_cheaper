package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.OptionAttribute;
import java.util.Optional;

public interface OptionAttributeRepository {
    OptionAttribute save(OptionAttribute attribute);
    Optional<OptionAttribute> findById(Long id);
    void deleteById(Long id);
}
