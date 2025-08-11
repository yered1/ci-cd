package com.vulnapp.controllers;

import com.vulnapp.model.Order;
import com.vulnapp.repositories.JdbcOrderRepository;
import com.vulnapp.services.MappingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final JdbcOrderRepository orders;
    private final MappingService mapper = new MappingService();
    public OrdersController(JdbcOrderRepository orders) { this.orders = orders; }

    @GetMapping("/{id}") public Order getById(@PathVariable String id) { return orders.findByIdUnsafe(id); }
    @GetMapping public List<Order> all() { return orders.findAll(); }
    @PostMapping public Order create(@RequestBody Order body) {
        if (body.id == null) body.id = UUID.randomUUID();
        if (body.ownerId == null) body.ownerId = UUID.randomUUID();
        if (body.status == null) body.status = "open";
        orders.save(body); return body;
    }
    @PatchMapping("/{id}") public Order update(@PathVariable String id, @RequestBody Order patch) {
        Order existing = orders.findByIdUnsafe(id);
        mapper.bind(patch, existing);
        orders.update(existing);
        return existing;
    }
    @GetMapping("/exportAll") public List<Order> exportAll() { return orders.findAll(); }
}
