package com.example.the_cheaper.dto.response.user;

import com.example.the_cheaper.dto.response.admin.AdminProductImageResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

import com.example.the_cheaper.dto.response.admin.AdminProductResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProductDetailResponse {
    private Long id;
    private String name;
    private String description;

    // Chỉ lấy tên để hiển thị, hoặc object đơn giản nếu cần ID để filter
    private String brand;
    private String category;
    private String material;

    // Giá hiển thị cho người dùng
    private BigDecimal price;          // Chính là salePrice
    private BigDecimal originalPrice;   // Chính là comparePrice
    private Integer discountPercentage; // Tính toán từ (compare - sale) / compare

    private List<UserImageResponse> images;
    private List<UserVariantInfoResponse> variants;

    private boolean isAvailable;
}


