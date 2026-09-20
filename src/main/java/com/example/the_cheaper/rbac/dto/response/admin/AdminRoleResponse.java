package com.example.the_cheaper.rbac.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleResponse {
    private Long id;
    private String name;
    private String description;
}
