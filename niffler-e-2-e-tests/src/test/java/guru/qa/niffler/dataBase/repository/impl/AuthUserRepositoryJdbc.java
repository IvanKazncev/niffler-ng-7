package guru.qa.niffler.dataBase.repository.impl;

import guru.qa.niffler.config.Config;

import guru.qa.niffler.dataBase.dbConnection.DataBases;
import guru.qa.niffler.dataBase.entity.AuthUserEntity;
import guru.qa.niffler.dataBase.entity.Authority;
import guru.qa.niffler.dataBase.entity.AuthorityEntity;
import guru.qa.niffler.dataBase.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.dataBase.repository.AuthUserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.dataBase.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {
    private static final Config CFG = Config.getInstance();




    @Override
    public AuthUserEntity createUser(AuthUserEntity authUser) {
        try (PreparedStatement userPs = holder(CFG.authJDBCUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs =  holder(CFG.authJDBCUrl()).connection().prepareStatement(
                     "INSERT INTO \"authority\" (authority, user_id) VALUES (?, ?)")){
                     userPs.setString(1, authUser.getUsername());
             userPs.setString(2, authUser.getPassword());
             userPs.setBoolean(3, authUser.getEnabled());
             userPs.setBoolean(4, authUser.getAccountNonExpired());
             userPs.setBoolean(5, authUser.getAccountNonLocked());
             userPs.setBoolean(6, authUser.getCredentialsNonExpired());
             userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Запрос не нашел ключи в БД");
                }
                authUser.setId(generatedKey);
                for (AuthorityEntity authorityEntity : authUser.getAuthorities()) {
                    authorityPs.setObject(2,generatedKey);
                    authorityPs.setString(1,authorityEntity.getAuthority().name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }
                authorityPs.executeBatch();
                return authUser;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try(PreparedStatement ps = holder(CFG.authJDBCUrl()).connection().prepareStatement(
                "select * from \"user\" u join authority a on u.id = a.user_id where u.id = ?"
        )){
            ps.setObject(1,id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    ae.setId(rs.getObject("a.id", UUID.class));
                    authorities.add(ae);

                }
                if (user == null) {
                    return Optional.empty();
                }else {
                    user.setAuthorities(authorities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (Connection connection = DataBases.connection(CFG.authJDBCUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "select * from \"user\" u join authority a on u.id = a.user_id"
             )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    ae.setId(rs.getObject("a.id", UUID.class));
                    authorities.add(ae);
                }
                if (user != null) {
                    user.setAuthorities(authorities);
                    return List.of(user);
                }else {
                    throw new AssertionError("Пользователь не найден");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching data", e);
        }
    }
}
