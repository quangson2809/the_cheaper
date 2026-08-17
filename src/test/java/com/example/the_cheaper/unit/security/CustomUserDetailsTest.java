package com.example.the_cheaper.unit.security;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CustomUserDetails;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void authorities_ShouldContainRoleAndPermissionAuthorities() {
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .email("admin@example.com")
                .passwordHash("encoded")
                .status(1)
                .build();

        CustomUserDetails details = new CustomUserDetails(
                account,
                CustomUserDetails.authorities(
                        "ADMIN",
                        List.of("PRODUCT_READ", "PRODUCT_UPDATE")));

        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "PRODUCT_READ", "PRODUCT_UPDATE");
    }

    @Test
    void authorities_ShouldReturnEmptyForMissingRoleAndPermissions() {
        assertThat(CustomUserDetails.authorities(null, List.of())).isEmpty();
    }
}
