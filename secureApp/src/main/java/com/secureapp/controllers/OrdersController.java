package com.secureapp.controllers;

import com.secureapp.dto.OrderPatch;
import com.secureapp.model.Order;
import com.secureapp.repositories.OrderRepository;
import com.secureapp.repositories.UserRepository;
import com.secureapp.services.AuthorizationGuard;
import com.secureapp.services.MappingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController @RequestMapping("/orders")
public class OrdersController {
    private final OrderRepository orders; private final UserRepository users; private final AuthorizationGuard guard;
    private final MappingService mapper = new MappingService();
    public OrdersController(OrderRepository orders, UserRepository users, AuthorizationGuard guard){
        this.orders=orders; this.users=users; this.guard=guard;
    }
    @GetMapping("/{id}")
    public Order getById(@PathVariable UUID id, Authentication auth) {
        var u = users.findByUsername(auth.getName()).orElseThrow();
        guard.checkOwnership(id, u.getId(), u.isAdmin());
        return orders.findById(id).orElseThrow();
    }
    @GetMapping
    public List<Order> myOrders(Authentication auth) {
        var u = users.findByUsername(auth.getName()).orElseThrow();
        return orders.findByOwnerId(u.getId());
    }
    @PostMapping
    public Order create(@Valid @RequestBody Order body, Authentication auth) {
        var u = users.findByUsername(auth.getName()).orElseThrow();
        body.setId(UUID.randomUUID());
        body.setOwnerId(u.getId());
        if (body.getStatus() == null) body.setStatus("open");
        return orders.save(body);
    }
    @PatchMapping("/{id}")
    public Order update(@PathVariable UUID id, @Valid @RequestBody OrderPatch patch, Authentication auth) {
        var u = users.findByUsername(auth.getName()).orElseThrow();
        guard.checkOwnership(id, u.getId(), u.isAdmin());
        var existing = orders.findById(id).orElseThrow();
        mapper.bindAllowList(patch, existing, Set.of("itemName","quantity","status"));
        return orders.save(existing);
    }
}
