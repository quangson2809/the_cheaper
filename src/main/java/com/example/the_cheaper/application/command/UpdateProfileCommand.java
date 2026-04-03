package com.example.the_cheaper.application.command;

public record UpdateProfileCommand(
    Long userId,
    String name,
    String email
) {}
