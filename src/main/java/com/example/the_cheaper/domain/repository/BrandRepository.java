package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.Brand;

public interface BrandRepository extends BaseRepository<Brand, Long> {
    boolean existsByName(String name);

}
