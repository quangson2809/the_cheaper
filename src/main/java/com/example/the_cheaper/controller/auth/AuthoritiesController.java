package com.example.the_cheaper.controller.auth;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.response.auth.AuthoritiesResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.service.authorization.AuthorizationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthoritiesController {
    private final AuthorizationQueryService authorization;

    @GetMapping("/api/auth/me/authorities")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AuthoritiesResponse> authorities(@CurrentUser AccountEntity account) {
        return ApiResponse.success(authorization.findEffectiveAuthorities(account.getId()), "Quyền hiệu lực của tài khoản");
    }
}
