package guru.qa.niffler.dataBase.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.entity.FriendshipEntity;
import guru.qa.niffler.dataBase.entity.FriendshipStatus;
import guru.qa.niffler.dataBase.entity.UserEntity;
import guru.qa.niffler.dataBase.mapper.UserEntityRowMapper;
import guru.qa.niffler.dataBase.repository.UseDataRepository;
import guru.qa.niffler.dataBase.tpl.DataSources;
import guru.qa.niffler.model.CurrencyValues;
import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static guru.qa.niffler.dataBase.tpl.Connections.holder;

public class UseDataRepositorySpringJdbc implements UseDataRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJDBCUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "select * from \"user\" join public.friendship f on \"user\".id = f.addressee_id where id = ?",
                        new ResultSetExtractor<UserEntity>() {
                            @Override
                            public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                                Map<UUID, UserEntity> userEntityMap = new ConcurrentHashMap<>();
                                UUID userId = null;
                                while (rs.next()) {
                                    FriendshipEntity friendship = new FriendshipEntity();
                                    UserEntity userEntityId = new UserEntity();
                                    userEntityId.setId(rs.getObject("id", UUID.class));
                                    userId = rs.getObject("id", UUID.class);
                                    UUID finalUserId = userId;
                                    UserEntity user = userEntityMap.computeIfAbsent(userId, id -> {
                                        UserEntity userEntity = new UserEntity();
                                        userEntity.setId(id);
                                        try {
                                            userEntity.setSurname(rs.getString("surname"));
                                            userEntity.setFirstname(rs.getString("firstname"));
                                            userEntity.setFullname(rs.getString("full_name"));
                                            userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                                            userEntity.setPhoto(rs.getBytes("photo"));
                                            userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        try {
                                            if (rs.getObject("requester_id", UUID.class).equals(finalUserId)) {
                                                friendship.setRequester(userEntity);
                                                UserEntity addressee = new UserEntity();
                                                addressee.setId(rs.getObject("addressee_id", UUID.class));
                                                friendship.setAddressee(addressee);
                                                friendship.setCreatedDate(rs.getDate("created_date"));
                                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                                userEntity.getFriendshipRequests().add(friendship);
                                            }
                                            if (rs.getObject("addressee_id", UUID.class).equals(finalUserId)) {
                                                friendship.setAddressee(userEntity);
                                                UserEntity requester = new UserEntity();
                                                requester.setId(rs.getObject("requester_id", UUID.class));
                                                friendship.setRequester(requester);
                                                friendship.setCreatedDate(rs.getDate("created_date"));
                                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                                userEntity.getFriendshipAddressees().add(friendship);
                                            }
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return userEntity;
                                    });
                                }
                                return userEntityMap.get(userId);
                            }
                        }, id));

    }


    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJDBCUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "select * from \"user\" join public.friendship f on \"user\".id = f.addressee_id where username = ?",
                        new ResultSetExtractor<UserEntity>() {
                            @Override
                            public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                                Map<UUID, UserEntity> userEntityMap = new ConcurrentHashMap<>();
                                UUID userId = null;
                                while (rs.next()) {
                                    FriendshipEntity friendship = new FriendshipEntity();
                                    UserEntity userEntityId = new UserEntity();
                                    userEntityId.setId(rs.getObject("id", UUID.class));
                                    userId = rs.getObject("id", UUID.class);
                                    UUID finalUserId = userId;
                                    UserEntity user = userEntityMap.computeIfAbsent(userId, id -> {
                                        UserEntity userEntity = new UserEntity();
                                        userEntity.setId(id);
                                        try {
                                            userEntity.setSurname(rs.getString("surname"));
                                            userEntity.setFirstname(rs.getString("firstname"));
                                            userEntity.setFullname(rs.getString("full_name"));
                                            userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                                            userEntity.setPhoto(rs.getBytes("photo"));
                                            userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        try {
                                            if (rs.getObject("requester_id", UUID.class).equals(finalUserId)) {
                                                friendship.setRequester(userEntity);
                                                UserEntity addressee = new UserEntity();
                                                addressee.setId(rs.getObject("addressee_id", UUID.class));
                                                friendship.setAddressee(addressee);
                                                friendship.setCreatedDate(rs.getDate("created_date"));
                                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                                userEntity.getFriendshipRequests().add(friendship);
                                            }
                                            if (rs.getObject("addressee_id", UUID.class).equals(finalUserId)) {
                                                friendship.setAddressee(userEntity);
                                                UserEntity requester = new UserEntity();
                                                requester.setId(rs.getObject("requester_id", UUID.class));
                                                friendship.setRequester(requester);
                                                friendship.setCreatedDate(rs.getDate("created_date"));
                                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                                userEntity.getFriendshipAddressees().add(friendship);
                                            }
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return userEntity;
                                    });
                                }
                                return userEntityMap.get(userId);
                            }
                        }, username));
    }

    @Override
    public void delete(UserEntity user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SneakyThrows
    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJDBCUrl()));
        return jdbcTemplate.query(
                "select * from \"user\" join public.friendship f on \"user\".id = f.addressee_id",
                new ResultSetExtractor<List<UserEntity>>() {
                    @Override
                    public List<UserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<UserEntity> userEntityList = new ArrayList<>();
                        while (rs.next()) {
                            FriendshipEntity friendship = new FriendshipEntity();
                            UserEntity userEntityId = new UserEntity();
                            UserEntity userEntity = new UserEntity();
                            userEntityId.setId(rs.getObject("id", UUID.class));
                            userEntity.setId(rs.getObject("id", UUID.class));
                            userEntity.setSurname(rs.getString("surname"));
                            userEntity.setFirstname(rs.getString("firstname"));
                            userEntity.setFullname(rs.getString("full_name"));
                            userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                            userEntity.setPhoto(rs.getBytes("photo"));
                            userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                            friendship.setRequester(userEntity);
                            friendship.setCreatedDate(rs.getDate("created_date"));
                            userEntity.getFriendshipRequests().add(friendship);
                            userEntityList.add(userEntity);

                        }
                        return userEntityList;
                    }
                });
    }
}


