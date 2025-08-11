package com.secureapp.services;

import com.secureapp.repositories.OrderRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthorizationGuard {
    private final OrderRepository orders;
    public AuthorizationGuard(OrderRepository orders){ this.orders=orders; }
    public void checkOwnership(UUID orderId, UUID userId, boolean isAdmin) {
        if (isAdmin) return;
        orders.findByIdAndOwnerId(orderId, userId).orElseThrow(() -> new AccessDeniedException("Not owner"));
    }
}
