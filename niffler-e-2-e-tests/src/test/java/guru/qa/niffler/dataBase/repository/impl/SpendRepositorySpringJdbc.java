package guru.qa.niffler.dataBase.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.dao.SpendDao;
import guru.qa.niffler.dataBase.entity.CategoryEntity;
import guru.qa.niffler.dataBase.entity.SpendEntity;
import guru.qa.niffler.dataBase.mapper.SpendEntityRowMapper;
import guru.qa.niffler.dataBase.tpl.DataSources;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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

public class SpendRepositorySpringJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJDBCUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spendEntity.getUsername());
            ps.setDate(2,new java.sql.Date(spendEntity.getSpendDate().getTime()));
            ps.setString(3, spendEntity.getCurrency().name());
            ps.setDouble(4, spendEntity.getAmount());
            ps.setString(5, spendEntity.getDescription());
            ps.setObject(6, spendEntity.getCategory().getId());
            return ps;
        }, kh);

        final UUID generationKey = (UUID) kh.getKeys().get("id");
        spendEntity.setId(generationKey);
        return spendEntity;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJDBCUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "select * from spend s join category c on c.id = s.category_id where s.id = ?",
                        new ResultSetExtractor<SpendEntity>() {
                            @Override
                            public SpendEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                                Map<UUID, SpendEntity> spends = new ConcurrentHashMap<>();
                                UUID spendId = null;
                                while (rs.next()) {
                                    spendId = rs.getObject("id", UUID.class);
                                    SpendEntity spendEntity = spends.computeIfAbsent(spendId, id -> {
                                        SpendEntity spend = new SpendEntity();
                                        spend.setId(id);
                                        try {
                                            spend.setSpendDate(rs.getDate("spend_date"));
                                            spend.setAmount(rs.getDouble("amount"));
                                            spend.setDescription(rs.getString("description"));
                                            spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                                            spend.setUsername(rs.getString("username"));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return spend;
                                    });
                                    CategoryEntity category = new CategoryEntity();
                                    category.setId(rs.getObject("category_id", UUID.class));
                                    category.setName(rs.getString("name"));
                                    category.setArchived(rs.getBoolean("archived"));
                                    spendEntity.setCategory(category);
                                }
                                return spends.get(spendId);
                            }
                        },
                        id
                )
        );
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJDBCUrl()));
        return jdbcTemplate.query(
                "select * from spend s join category c on c.id = s.category_id where s.username = ?",
                new ResultSetExtractor<List<SpendEntity>>() {
                    @Override
                    public List<SpendEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<SpendEntity> spends = new ArrayList<>();
                        while (rs.next()) {
                            SpendEntity spend = new SpendEntity();
                            spend.setId(rs.getObject("id", UUID.class));
                            spend.setSpendDate(rs.getDate("spend_date"));
                            spend.setAmount(rs.getDouble("amount"));
                            spend.setDescription(rs.getString("description"));
                            spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                            spend.setUsername(rs.getString("username"));
                            CategoryEntity category = new CategoryEntity();
                            category.setId(rs.getObject("category_id", UUID.class));
                            category.setName(rs.getString("name"));
                            category.setArchived(rs.getBoolean("archived"));
                            spend.setCategory(category);
                            spends.add(spend);
                        }
                        return spends;
                    }
                },
                username
        );
    }

    @Override
    public void deleteSpend(SpendEntity spendEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJDBCUrl()));
        jdbcTemplate.batchUpdate(
                "DELETE FROM spend WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, String.valueOf(spendEntity.getId()));
                    }

                    @Override
                    public int getBatchSize() {
                        return 1;
                    }
                }
        );
    }

    @Override
    public List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJDBCUrl()));
        return jdbcTemplate.query(
                "select * from spend s join category c on c.id = s.category_id",
                new ResultSetExtractor<List<SpendEntity>>() {
                    @Override
                    public List<SpendEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<SpendEntity> spends = new ArrayList<>();
                        while (rs.next()) {
                            SpendEntity spend = new SpendEntity();
                            spend.setId(rs.getObject("id", UUID.class));
                            spend.setSpendDate(rs.getDate("spend_date"));
                            spend.setAmount(rs.getDouble("amount"));
                            spend.setDescription(rs.getString("description"));
                            spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                            spend.setUsername(rs.getString("username"));
                            CategoryEntity category = new CategoryEntity();
                            category.setId(rs.getObject("category_id", UUID.class));
                            category.setName(rs.getString("name"));
                            category.setArchived(rs.getBoolean("archived"));
                            spend.setCategory(category);
                            spends.add(spend);
                        }
                        return spends;
                    }
                }
        );
    }
}
