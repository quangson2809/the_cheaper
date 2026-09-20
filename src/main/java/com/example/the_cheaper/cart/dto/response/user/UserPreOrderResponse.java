package com.example.the_cheaper.cart.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreOrderResponse {
    private List<UserPreOrderItemResponse> items;
}
