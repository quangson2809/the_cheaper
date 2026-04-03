package com.example.the_cheaper.interfaces.rest.dto.response.user;

import com.example.the_cheaper.interfaces.rest.dto.response.admin.ProductResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductDetailResponse extends ProductResponse {
    private List<ReviewResponse> reviews;
}
