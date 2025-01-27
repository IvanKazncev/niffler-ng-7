package guru.qa.niffler.dataBase.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.dao.SpendDao;
import guru.qa.niffler.dataBase.dbConnection.DataBases;
import guru.qa.niffler.dataBase.entity.CategoryEntity;
import guru.qa.niffler.dataBase.entity.SpendEntity;
import guru.qa.niffler.dataBase.mapper.SpendEntityRowMapper;
import guru.qa.niffler.dataBase.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;
import org.hibernate.AssertionFailure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.dataBase.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();


    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        try (PreparedStatement ps = holder(CFG.spendJDBCUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spendEntity.getUsername());
            ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            ps.setString(3, spendEntity.getCurrency().name());
            ps.setDouble(4, spendEntity.getAmount());
            ps.setString(5, spendEntity.getDescription());
            ps.setObject(6, spendEntity.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Запрос не нашел ключи в БД");
                }
            }
            spendEntity.setId(generatedKey);
            return spendEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJDBCUrl()).connection().prepareStatement(
                "select * from spend s join public.category c on c.id = s.category_id where s.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                SpendEntity spendEntity = null;
                List<CategoryEntity> categories = new ArrayList<>();
                while (rs.next()) {
                    if (spendEntity == null) {
                        spendEntity = SpendEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("category_id", UUID.class));
                    ce.setUsername(spendEntity.getUsername());
                    ce.setName(rs.getString("description"));
                    ce.setArchived(rs.getBoolean("archived"));
                    categories.add(ce);
                }
                if (spendEntity != null) {
                    return Optional.of(spendEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        List<SpendEntity> spendEntities = new ArrayList<>();
        try (Connection connection = DataBases.connection(CFG.spendJDBCUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "select * from spend s join public.category c on c.id = s.category_id where s.username = ?"
             )) {

            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                 spendEntities.add(SpendEntityRowMapper.instance.mapRow(rs, 1));
                }
             return spendEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching spend data", e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spendEntity) {
        try (Connection connection = DataBases.connection(CFG.spendJDBCUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM spend WHERE id = ?"
             )) {
            ps.setObject(1, spendEntity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        List<SpendEntity> spendEntities = new ArrayList<>();
        try (Connection connection = DataBases.connection(CFG.spendJDBCUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "select * from spend s join public.category c on c.id = s.category_id"
             )) {
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    spendEntities.add(SpendEntityRowMapper.instance.mapRow(rs, 1));
                }
                return spendEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching spend data", e);
        }
    }
}
