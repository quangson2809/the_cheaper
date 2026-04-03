package com.example.the_cheaper.application.command;

import java.math.BigDecimal;
import java.util.List;

public record VariantCommand(
    String sku,
    int stock,
    BigDecimal overiteSalePrice,
    List<Long> optionValueIds
) {}
