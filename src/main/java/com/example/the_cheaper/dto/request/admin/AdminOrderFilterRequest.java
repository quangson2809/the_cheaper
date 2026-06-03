package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderFilterRequest {
    private String status;
    @NotNull(message = "Page không được null")
    private int page = 1;
    @NotNull(message = "limit không được null")
    private int limit = 10;
}

