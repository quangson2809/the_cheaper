package com.example.the_cheaper.application.command;

public record UpdateCategoryCommand(
    Long id,
    String name
) {}
