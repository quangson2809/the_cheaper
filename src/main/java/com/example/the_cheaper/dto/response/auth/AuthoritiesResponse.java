package com.example.the_cheaper.dto.response.auth;

import java.util.List;

public record AuthoritiesResponse(List<String> roles, List<String> permissions) {}
