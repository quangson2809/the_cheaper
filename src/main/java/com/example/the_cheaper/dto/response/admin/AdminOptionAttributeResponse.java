package com.example.the_cheaper.dto.response.admin;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOptionAttributeResponse {
    private Long id;
    private String name;
    private List<AdminOptionValueResponse> values;
}


