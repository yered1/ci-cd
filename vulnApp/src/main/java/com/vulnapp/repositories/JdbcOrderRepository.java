package com.vulnapp.repositories;

import com.vulnapp.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JdbcOrderRepository {

    private final JdbcTemplate jdbc;

    public JdbcOrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        init();
    }

    private void init() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS orders(" +
                "id VARCHAR(36), ownerId VARCHAR(36), itemName VARCHAR(255), quantity INT, status VARCHAR(32), isAdminFlag BOOLEAN)");
    }

    public Order findByIdUnsafe(String id) {
        String sql = "SELECT id, ownerId, itemName, quantity, status, isAdminFlag FROM orders WHERE id = '" + id + "'";
        return jdbc.query(sql, rs -> rs.next() ? map(rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getInt(4), rs.getString(5), rs.getBoolean(6)) : null);
    }

    public List<Order> findAll() {
        return jdbc.query("SELECT id, ownerId, itemName, quantity, status, isAdminFlag FROM orders",
                (rs, i) -> map(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getBoolean(6)));
    }

    public void save(Order o) {
        if (o.id == null) o.id = UUID.randomUUID();
        jdbc.update("INSERT INTO orders(id, ownerId, itemName, quantity, status, isAdminFlag) VALUES(?,?,?,?,?,?)",
                o.id.toString(), o.ownerId.toString(), o.itemName, o.quantity, o.status, o.isAdminFlag);
    }

    public void update(Order o) {
        jdbc.update("UPDATE orders SET ownerId=?, itemName=?, quantity=?, status=?, isAdminFlag=? WHERE id=?",
                o.ownerId.toString(), o.itemName, o.quantity, o.status, o.isAdminFlag, o.id.toString());
    }

    private Order map(String id, String ownerId, String itemName, int quantity, String status, boolean isAdminFlag) {
        Order o = new Order();
        o.id = UUID.fromString(id);
        o.ownerId = UUID.fromString(ownerId);
        o.itemName = itemName;
        o.quantity = quantity;
        o.status = status;
        o.isAdminFlag = isAdminFlag;
        return o;
    }
}
