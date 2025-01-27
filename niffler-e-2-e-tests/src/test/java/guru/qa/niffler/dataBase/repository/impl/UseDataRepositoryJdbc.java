package guru.qa.niffler.dataBase.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.entity.*;
import guru.qa.niffler.dataBase.mapper.UserEntityRowMapper;
import guru.qa.niffler.dataBase.repository.UseDataRepository;
import guru.qa.niffler.model.CurrencyValues;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.dataBase.tpl.Connections.holder;

public class UseDataRepositoryJdbc implements UseDataRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJDBCUrl()).connection().prepareStatement(
                "select * from \"user\" join public.friendship f on \"user\".id = f.addressee_id where id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                UserEntity user = null;
                while (rs.next()) {
                    if (user == null) {
                        user = UserEntityRowMapper.instance.mapRow(rs, 1);
                        if (rs.getObject("created_date") != null) {
                            FriendshipEntity friendship = new FriendshipEntity();

                            if (rs.getObject("requester_id", UUID.class).equals(user.getId())) {
                                friendship.setRequester(user);
                                UserEntity addressee = new UserEntity();
                                addressee.setId(rs.getObject("addressee_id", UUID.class));
                                friendship.setAddressee(addressee);
                                friendship.setCreatedDate(rs.getDate("created_date"));
                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                user.getFriendshipRequests().add(friendship);
                            }

                            if (rs.getObject("addressee_id", UUID.class).equals(user.getId())) {
                                friendship.setAddressee(user);
                                UserEntity requester = new UserEntity();
                                requester.setId(rs.getObject("requester_id", UUID.class));
                                friendship.setRequester(requester);
                                friendship.setCreatedDate(rs.getDate("created_date"));
                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                user.getFriendshipAddressees().add(friendship);
                            }
                        }
                    }
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJDBCUrl()).connection().prepareStatement(
                "select * from \"user\" join public.friendship f on \"user\".id = f.addressee_id where username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                UserEntity user = null;
                while (rs.next()) {
                    if (user == null) {
                        user = UserEntityRowMapper.instance.mapRow(rs, 1);
                        if (rs.getObject("created_date") != null) {
                            FriendshipEntity friendship = new FriendshipEntity();

                            if (rs.getObject("requester_id", UUID.class).equals(user.getId())) {
                                friendship.setRequester(user);
                                UserEntity addressee = new UserEntity();
                                addressee.setId(rs.getObject("addressee_id", UUID.class));
                                friendship.setAddressee(addressee);
                                friendship.setCreatedDate(rs.getDate("created_date"));
                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                user.getFriendshipRequests().add(friendship);
                            }

                            if (rs.getObject("addressee_id", UUID.class).equals(user.getId())) {
                                friendship.setAddressee(user);
                                UserEntity requester = new UserEntity();
                                requester.setId(rs.getObject("requester_id", UUID.class));
                                friendship.setRequester(requester);
                                friendship.setCreatedDate(rs.getDate("created_date"));
                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                user.getFriendshipAddressees().add(friendship);
                            }
                        }
                    }
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SneakyThrows
    @Override
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.userdataJDBCUrl()).connection().prepareStatement(
                "select * from \"user\" join public.friendship f on \"user\".id = f.addressee_id"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<UserEntity> users = new ArrayList<>();
                UserEntity user = null;
                while (rs.next()) {
                    if (user == null) {
                        user = UserEntityRowMapper.instance.mapRow(rs, 1);

                        if (rs.getObject("created_date") != null) {
                            FriendshipEntity friendship = new FriendshipEntity();

                            if (rs.getObject("requester_id", UUID.class).equals(user.getId())) {
                                friendship.setRequester(user);
                                UserEntity addressee = new UserEntity();
                                addressee.setId(rs.getObject("addressee_id", UUID.class));
                                friendship.setAddressee(addressee);
                                friendship.setCreatedDate(rs.getDate("created_date"));
                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                user.getFriendshipRequests().add(friendship);
                            }

                            if (rs.getObject("addressee_id", UUID.class).equals(user.getId())) {
                                friendship.setAddressee(user);
                                UserEntity requester = new UserEntity();
                                requester.setId(rs.getObject("requester_id", UUID.class));
                                friendship.setRequester(requester);
                                friendship.setCreatedDate(rs.getDate("created_date"));
                                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                                user.getFriendshipAddressees().add(friendship);
                            }
                        }
                        users.add(user);
                    }
                }
                return users;
            }
        }
    }
}


