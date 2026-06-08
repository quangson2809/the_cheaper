package com.example.the_cheaper.fixtures;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.CartEntity;
import com.example.the_cheaper.entity.RoleEntity;

public class AccountFixtures {

    public static RoleEntity createUserRole() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("USER");
        return role;
    }

    public static RoleEntity createAdminRole() {
        RoleEntity role = new RoleEntity();
        role.setId(2L);
        role.setName("ADMIN");
        return role;
    }

    public static AccountEntity createActiveUserAccount() {
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .passwordHash("hashed_password")
                .role(createUserRole())
                .status(1)
                .rewardPoint(100)
                .build();
        
        CartEntity cart = new CartEntity();
        cart.setId(1L);
        cart.setAccount(account);
        account.setCart(cart);
        
        return account;
    }

    public static AccountEntity createActiveAdminAccount() {
        AccountEntity account = AccountEntity.builder()
                .id(2L)
                .name("Test Admin")
                .email("admin@test.com")
                .passwordHash("hashed_password")
                .role(createAdminRole())
                .status(1)
                .rewardPoint(0)
                .build();
        return account;
    }
}
