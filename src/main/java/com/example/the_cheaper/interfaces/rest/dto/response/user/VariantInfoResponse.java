package com.example.the_cheaper.interfaces.rest.dto.response.user;

import com.example.the_cheaper.interfaces.rest.dto.response.admin.OptionValueResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantInfoResponse {
    private Long variantId;
    private String optionValue;
}
