package com.example.the_cheaper.rbac.dto.request.admin;


import com.example.the_cheaper.rbac.entity.Role;
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
