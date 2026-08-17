package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignAccountRoleRequest {
    @NotNull(message = "roleId không được để trống")
    private Long roleId;
}
