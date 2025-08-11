package com.secureapp.graphql;

import com.secureapp.model.Order;
import com.secureapp.repositories.OrderRepository;
import com.secureapp.repositories.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import java.util.List; import java.util.UUID;

@Controller
public class QueryResolver {
    private final OrderRepository orders; private final UserRepository users;
    public QueryResolver(OrderRepository orders, UserRepository users) { this.orders=orders; this.users=users; }
    @QueryMapping public List<Order> myOrders(@AuthenticationPrincipal User user) {
        var u = users.findByUsername(user.getUsername()).orElseThrow();
        return orders.findByOwnerId(u.getId());
    }
    @QueryMapping public Order order(@Argument String id, @AuthenticationPrincipal User user) {
        var u = users.findByUsername(user.getUsername()).orElseThrow();
        return orders.findByIdAndOwnerId(UUID.fromString(id), u.getId()).orElse(null);
    }
}
