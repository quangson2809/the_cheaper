package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionAttributeResponse {
    private Long id;
    private String name;
    private List<OptionValueResponse> values;
}
