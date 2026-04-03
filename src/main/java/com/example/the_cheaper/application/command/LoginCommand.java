package com.example.the_cheaper.application.command;

public record LoginCommand(
    String email,
    String password
) {}
