package com.example.the_cheaper.application.command;

public record RegisterUserCommand(
    String name,
    String email,
    String password
) {}
