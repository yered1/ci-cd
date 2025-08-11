package com.vulnapp.repositories;

import com.vulnapp.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JdbcUserRepository {

    private final JdbcTemplate jdbc;

    public JdbcUserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        init();
    }

    private void init() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS users(" +
                "id VARCHAR(36), username VARCHAR(255), passwordHash VARCHAR(255), admin BOOLEAN, tenantId VARCHAR(64), jwtToken VARCHAR(512))");
        insertIfNotExists("admin", "21232f297a57a5a743894a0e4a801fc3", true, "t1"); // md5("admin")
        insertIfNotExists("alice", "e1faffb3e614e6c2fba74296962386b7", false, "t1"); // md5("password1")
        insertIfNotExists("bob",   "5f4dcc3b5aa765d61d8327deb882cf99", false, "t2"); // md5("password")
    }

    private void insertIfNotExists(String username, String hash, boolean admin, String tenant) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        if (count != null && count == 0) {
            jdbc.update("INSERT INTO users(id, username, passwordHash, admin, tenantId, jwtToken) VALUES(?,?,?,?,?,?)",
                    UUID.randomUUID().toString(), username, hash, admin, tenant, "");
        }
    }

    public User findByUsernameUnsafe(String username) {
        String sql = "SELECT id, username, passwordHash, admin, tenantId, jwtToken FROM users WHERE username = '" + username + "'";
        return jdbc.query(sql, rs -> rs.next() ? map(rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getBoolean(4), rs.getString(5), rs.getString(6)) : null);
    }

    public List<User> findAll() {
        return jdbc.query("SELECT id, username, passwordHash, admin, tenantId, jwtToken FROM users",
                (rs, i) -> map(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getBoolean(4), rs.getString(5), rs.getString(6)));
    }

    public void saveToken(UUID id, String token) {
        jdbc.update("UPDATE users SET jwtToken = ? WHERE id = ?", token, id.toString());
    }

    private User map(String id, String username, String ph, boolean admin, String tenant, String jwt) {
        User u = new User(UUID.fromString(id), username, ph, admin, tenant);
        u.jwtToken = jwt;
        return u;
    }
}
