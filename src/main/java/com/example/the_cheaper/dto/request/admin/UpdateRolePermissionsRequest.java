package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolePermissionsRequest {
    @NotNull(message = "Permission ids are required")
    private List<Long> permissionIds;
}
