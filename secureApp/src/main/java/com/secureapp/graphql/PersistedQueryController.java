package com.secureapp.graphql;

import com.secureapp.model.Order;
import com.secureapp.repositories.OrderRepository;
import com.secureapp.repositories.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/graphql")
public class PersistedQueryController {

    private final OrderRepository orders;
    private final UserRepository users;

    public PersistedQueryController(OrderRepository orders, UserRepository users) {
        this.orders = orders;
        this.users = users;
    }

    /**
     * Persisted queries implemented via REST (engine-free) to avoid version API drift.
     * id=q1 -> { myOrders { id itemName quantity status } }
     * id=q2 -> query($id: ID!){ order(id:$id){ id itemName quantity status } }
     */
    @PostMapping(path="/pq", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> runPersisted(@RequestParam String id,
                                            @RequestBody(required = false) Map<String, Object> variables,
                                            @AuthenticationPrincipal User principal) {
        var u = users.findByUsername(principal.getUsername()).orElseThrow();

        if ("q1".equals(id)) {
            List<Order> mine = orders.findByOwnerId(u.getId());
            return Map.of(
                    "data", Map.of(
                            "myOrders", mine.stream().map(o -> Map.of(
                                    "id", o.getId().toString(),
                                    "itemName", o.getItemName(),
                                    "quantity", o.getQuantity(),
                                    "status", o.getStatus()
                            )).toList()
                    )
            );
        }

        if ("q2".equals(id)) {
            String orderId = null;
            if (variables != null && variables.get("variables") instanceof Map<?,?> top) {
                Object inner = ((Map<?,?>) top).get("id");
                orderId = inner != null ? inner.toString() : null;
            } else if (variables != null) {
                Object inner = variables.get("id");
                orderId = inner != null ? inner.toString() : null;
            }
            if (orderId == null || orderId.isBlank()) {
                return Map.of("errors", List.of(Map.of("message", "missing variable: id")));
            }
            var found = orders.findByIdAndOwnerId(UUID.fromString(orderId), u.getId()).orElse(null);
            return Map.of(
                    "data", Map.of(
                            "order", found == null ? null : Map.of(
                                    "id", found.getId().toString(),
                                    "itemName", found.getItemName(),
                                    "quantity", found.getQuantity(),
                                    "status", found.getStatus()
                            )
                    )
            );
        }

        return Map.of("errors", List.of(Map.of("message", "unknown query id")));
    }
}
