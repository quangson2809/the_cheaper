package com.example.the_cheaper.application.command;

public record UpdateCartItemCommand(
    Long userId,
    Long cartItemId,
    int quantity
) {}
