package com.example.the_cheaper.interfaces.rest.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionAttributeRequest {
    @NotBlank(message = "Attribute name is required")
    private String name;
    private List<OptionValueRequest> values;
}
