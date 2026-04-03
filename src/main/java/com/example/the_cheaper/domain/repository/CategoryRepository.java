package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.Category;

public interface CategoryRepository extends BaseRepository<Category, Long> {
    boolean existsByName(String name);
}
