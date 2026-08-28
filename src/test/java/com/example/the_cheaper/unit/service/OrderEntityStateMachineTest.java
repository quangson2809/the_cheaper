package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderEntityStateMachineTest {

    @Test
    void pendingAllowsProcessingAndCancellation() {
        OrderEntity order = OrderEntity.builder().status(OrderStatus.PENDING).build();

        assertTrue(order.canTransitionTo(OrderStatus.PROCESSING));
        assertTrue(order.canTransitionTo(OrderStatus.CANCELED));
        assertFalse(order.canTransitionTo(OrderStatus.SHIPPING));
    }

    @Test
    void processingAllowsShippingAndCancellation() {
        OrderEntity order = OrderEntity.builder().status(OrderStatus.PROCESSING).build();

        assertTrue(order.canTransitionTo(OrderStatus.SHIPPING));
        assertTrue(order.canTransitionTo(OrderStatus.CANCELED));
        assertFalse(order.canTransitionTo(OrderStatus.DELIVERED));
    }

    @Test
    void deliveredAndCanceledAreTerminal() {
        OrderEntity delivered = OrderEntity.builder().status(OrderStatus.DELIVERED).build();
        OrderEntity canceled = OrderEntity.builder().status(OrderStatus.CANCELED).build();

        assertFalse(delivered.canTransitionTo(OrderStatus.CANCELED));
        assertFalse(delivered.canTransitionTo(OrderStatus.REFUNDED));
        assertFalse(canceled.canTransitionTo(OrderStatus.PENDING));
    }

    @Test
    void transitionToRejectsInvalidTransition() {
        OrderEntity order = OrderEntity.builder().status(OrderStatus.DELIVERED).build();

        assertThrows(IllegalStateException.class, () -> order.transitionTo(OrderStatus.CANCELED));
    }

    @Test
    void statusCannotBeMutatedThroughPublicSetter() {
        assertThrows(NoSuchMethodException.class,
                () -> OrderEntity.class.getMethod("setStatus", OrderStatus.class));

        Method transitionMethod;
        try {
            transitionMethod = OrderEntity.class.getMethod("transitionTo", OrderStatus.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("OrderEntity must expose transitionTo as the status mutation API", e);
        }

        assertTrue(java.lang.reflect.Modifier.isPublic(transitionMethod.getModifiers()));
    }
}
