package com.example.the_cheaper.application.command;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
    String name,
    String description,
    String material,
    BigDecimal salePrice,
    BigDecimal comparePrice,
    Long brandId,
    Long categoryId,
    List<VariantCommand> variants,
    List<ProductImageCommand> images,
    List<OptionAttributeCommand> optionAttributes
) {}
