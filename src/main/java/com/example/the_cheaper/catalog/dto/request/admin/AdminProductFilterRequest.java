package com.example.the_cheaper.catalog.dto.request.admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductFilterRequest {
    private Long brandId, categoryId,materialId;
    private Integer status;
    int page=1,limit=10;
    String sortBy ="price";
}


