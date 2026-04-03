package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    private Long id;
    private String name;
    private String alt;
}
