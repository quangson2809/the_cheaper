package com.example.the_cheaper.application.command;

public record PlaceOrderCommand(
    Long userId,
    String receiver,
    String phone,
    String email,
    String location,
    String paymentMethod
) {}
