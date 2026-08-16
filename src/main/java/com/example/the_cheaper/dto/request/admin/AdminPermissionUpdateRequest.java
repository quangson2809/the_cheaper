package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermissionUpdateRequest {

    @NotBlank(message = "Permission name is required")
    private String name;

    @NotBlank(message = "Permission code is required")
    private String code;

    private String description;
}
