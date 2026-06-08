package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOptionValueRequest {
    private Long id;
    @NotBlank(message = "Value is required")
    private String value;
}

