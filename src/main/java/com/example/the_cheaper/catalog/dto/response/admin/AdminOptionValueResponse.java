package com.example.the_cheaper.catalog.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOptionValueResponse {
    private Long id;
    private String attributeName;
    private String value;
}

