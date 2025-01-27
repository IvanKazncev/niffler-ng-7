package guru.qa.niffler.dataBase.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.entity.AuthUserEntity;
import guru.qa.niffler.dataBase.entity.Authority;
import guru.qa.niffler.dataBase.entity.AuthorityEntity;

import guru.qa.niffler.dataBase.repository.AuthUserRepository;
import guru.qa.niffler.dataBase.tpl.DataSources;

import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity createUser(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJDBCUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, authUser.getUsername());
            ps.setString(2, authUser.getPassword());
            ps.setBoolean(3, authUser.getEnabled());
            ps.setBoolean(4, authUser.getAccountNonExpired());
            ps.setBoolean(5, authUser.getAccountNonLocked());
            ps.setBoolean(6, authUser.getCredentialsNonExpired());
            return ps;
        }, kh);
        final UUID generationKey = (UUID) kh.getKeys().get("id");
        authUser.setId(generationKey);
        return authUser;
    }


    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJDBCUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "select * from \"user\" u join authority a on u.id = a.user_id where u.id = ?",
                        new ResultSetExtractor<AuthUserEntity>() {
                            @Override
                            public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                                Map<UUID, AuthUserEntity> users = new ConcurrentHashMap<>();
                                UUID userId = null;
                                while (rs.next()) {
                                    userId = rs.getObject("id", UUID.class);
                                    AuthUserEntity user = users.computeIfAbsent(userId, id -> {
                                        AuthUserEntity authUser = new AuthUserEntity();
                                        authUser.setId(id);
                                        try {
                                            authUser.setUsername(rs.getString("username"));
                                            authUser.setPassword(rs.getString("password"));
                                            authUser.setEnabled(rs.getBoolean("enabled"));
                                            authUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                                            authUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                                            authUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return authUser;
                                    });
                                    AuthorityEntity authority = new AuthorityEntity();
                                    authority.setId(rs.getObject("id", UUID.class));
                                    authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                                    user.addAuthorities(authority);
                                }
                                return users.get(userId);

                            }
                        },
                        id
                )
        );
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJDBCUrl()));
        return jdbcTemplate.query(
                "select * from \"user\" u join authority a on u.id = a.user_id",
                new ResultSetExtractor<List<AuthUserEntity>>() {
                    @Override
                    public List<AuthUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<AuthUserEntity> users = new ArrayList<>();
                        while (rs.next()) {
                            AuthUserEntity user = new AuthUserEntity();
                            AuthorityEntity authority = new AuthorityEntity();
                            user.setId(rs.getObject("id", UUID.class));
                            user.setUsername(rs.getString("username"));
                            user.setPassword(rs.getString("password"));
                            user.setEnabled(rs.getBoolean("enabled"));
                            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                            authority.setId(rs.getObject("id", UUID.class));
                            authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                            user.addAuthorities(authority);
                            users.add(user);
                        }
                        return users;
                    }
                }
        );
    }

}
