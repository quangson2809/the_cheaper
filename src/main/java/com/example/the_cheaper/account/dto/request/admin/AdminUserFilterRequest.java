package com.example.the_cheaper.account.dto.request.admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserFilterRequest {
    private Integer status;
    private String role;
    int page=1,limit=10;
}


