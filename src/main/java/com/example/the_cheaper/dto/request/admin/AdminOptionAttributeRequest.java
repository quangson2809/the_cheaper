package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOptionAttributeRequest {
    @NotBlank(message = "Attribute name is required")
    private String name;
    private List<AdminOptionValueRequest> values;
}


