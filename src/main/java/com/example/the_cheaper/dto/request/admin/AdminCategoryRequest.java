package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    private Integer status;
}

