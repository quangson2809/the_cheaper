package com.example.the_cheaper.dto.request.admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductFilterRequest {
    private Long brandId, categoryId;
    int page,limit;
}


