package com.example.the_cheaper.application.command;

import java.util.List;

public record OptionAttributeCommand(
    String name,
    List<OptionValueCommand> values
) {}
