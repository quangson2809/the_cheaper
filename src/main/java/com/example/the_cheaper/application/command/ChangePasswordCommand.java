package com.example.the_cheaper.application.command;

public record ChangePasswordCommand(
    Long id,
    String oldPassword,
    String newPassword
) {}
