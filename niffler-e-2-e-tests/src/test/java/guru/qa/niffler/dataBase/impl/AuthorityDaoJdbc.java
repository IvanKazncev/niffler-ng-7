package guru.qa.niffler.dataBase.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.dao.AuthorityDao;
import guru.qa.niffler.dataBase.dbConnection.DataBases;
import guru.qa.niffler.dataBase.entity.Authority;
import guru.qa.niffler.dataBase.entity.AuthorityEntity;
import guru.qa.niffler.dataBase.tpl.JdbcConnectionHolder;
import guru.qa.niffler.dataBase.tpl.JdbcConnectionHolders;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.dataBase.tpl.Connections.holder;

public class AuthorityDaoJdbc implements AuthorityDao {

    private static final Config CFG = Config.getInstance();





    @Override
    public AuthorityEntity createUser(AuthorityEntity...authority) {
        try (PreparedStatement ps =  holder(CFG.authJDBCUrl()).connection().prepareStatement(
                "INSERT INTO \"authority\" (authority, user_id) VALUES (?, ?)"
        )) {
            for (AuthorityEntity authorityEntity : authority) {
                ps.setObject(1,authorityEntity.getUser().getId());
                ps.setString(2,authorityEntity.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authority[0];
    }

    @Override
    public List<AuthorityEntity> findAll() {
        List<AuthorityEntity> authorityEntities = new ArrayList<>();
        try (Connection connection = DataBases.connection(CFG.authJDBCUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM authority"
             )) {
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setId(rs.getObject("id", UUID.class));
                    authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(authority);
                }
                return authorityEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching data", e);
        }
    }
}
