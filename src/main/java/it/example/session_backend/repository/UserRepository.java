
package it.example.session_backend.repository;

import it.example.session_backend.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) ->
        new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("password_hash")
        );

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUsername(String username) {
        return jdbcTemplate.query(
            "SELECT id, username, password_hash FROM users WHERE username = ?",
            userRowMapper,
            username
        ).stream().findFirst();
    }

    public User save(String username, String passwordHash) {
        return jdbcTemplate.queryForObject(
            """
            INSERT INTO users (username, password_hash)
            VALUES (?, ?)
            RETURNING id, username, password_hash
            """,
            userRowMapper,
            username,
            passwordHash
        );
    }
}
