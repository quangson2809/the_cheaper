package com.example.the_cheaper.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryResponse {
    private Long id;
    private String name;
    private String status;
}

