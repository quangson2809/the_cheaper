package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleCreateRequest {

    @NotBlank(message = "Role name is required")
    private String name;

    private String description;
}
