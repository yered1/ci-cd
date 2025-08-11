package com.secureapp.repositories;

import com.secureapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByOwnerId(UUID ownerId);
    Optional<Order> findByIdAndOwnerId(UUID id, UUID ownerId);
}
