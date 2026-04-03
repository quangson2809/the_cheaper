package com.example.the_cheaper.interfaces.rest.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionValueRequest {
    @NotBlank(message = "Value is required")
    private String value;
}
