package com.example.the_cheaper.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermissionResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
}
