package com.example.the_cheaper.application.query;

import java.math.BigDecimal;

public record SearchProductQuery(
    String name,
    Long categoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    int page,
    int limit
) {}
