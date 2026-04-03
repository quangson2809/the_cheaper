package com.example.the_cheaper.application.command;

public record CreateReviewCommand(
    Long userId,
    Long productId,
    String content
) {}
