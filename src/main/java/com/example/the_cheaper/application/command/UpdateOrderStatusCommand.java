package com.example.the_cheaper.application.command;

import com.example.the_cheaper.domain.model.OrderStatus;

public record UpdateOrderStatusCommand(
    Long id,
    OrderStatus status
) {}
