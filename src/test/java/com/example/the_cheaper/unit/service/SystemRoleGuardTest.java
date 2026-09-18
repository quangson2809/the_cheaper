package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.authorization.SystemRoleGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemRoleGuardTest {
    @Mock RoleRepository roles;
    @Mock AccountRepository accounts;
    @InjectMocks SystemRoleGuard guard;

    @Test void lastActiveAdminIsProtectedUsingResolvedRoleAndAccountIds() {
        when(roles.lockByName("ADMIN")).thenReturn(Optional.of(RoleEntity.builder().id(73L).name("ADMIN").build()));
        when(accounts.lockActiveAccountsByRole(73L)).thenReturn(List.of(AccountEntity.builder().id(98L).status(1).build()));
        assertThatThrownBy(() -> guard.requireAnotherActiveAdmin(98L)).isInstanceOf(InvalidInputException.class);
        assertThatCode(() -> guard.requireAnotherActiveAdmin(99L)).doesNotThrowAnyException();
        var ordered = inOrder(roles, accounts);
        ordered.verify(roles).lockByName("ADMIN");
        ordered.verify(accounts).lockActiveAccountsByRole(73L);
    }

    @Test void anotherActiveAdminAllowsRemoval() {
        when(roles.lockByName("ADMIN")).thenReturn(Optional.of(RoleEntity.builder().id(73L).name("ADMIN").build()));
        when(accounts.lockActiveAccountsByRole(73L)).thenReturn(List.of(
                AccountEntity.builder().id(98L).status(1).build(), AccountEntity.builder().id(99L).status(1).build()));
        assertThatCode(() -> guard.requireAnotherActiveAdmin(98L)).doesNotThrowAnyException();
    }
}
