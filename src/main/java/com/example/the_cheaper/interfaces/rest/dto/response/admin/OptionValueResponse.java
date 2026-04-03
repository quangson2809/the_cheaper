package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionValueResponse {
    private Long id;
    private String attributeName;
    private String value;
}
