package com.example.the_cheaper.application.command;

public record AddToCartCommand(
    Long userId,
    Long variantId,
    int quantity
) {}
