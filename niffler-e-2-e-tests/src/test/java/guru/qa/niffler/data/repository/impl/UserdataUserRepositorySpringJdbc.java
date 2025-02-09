package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    @Nonnull
    @Override
    public UserEntity create(UserEntity user) {
        return udUserDao.create(user);
    }

    @Nonnull
    @Override
    public UserEntity update(UserEntity user) {
        return udUserDao.update(user);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        return udUserDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public void sendInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        requester.addFriends(FriendshipStatus.PENDING, addressee);
        udUserDao.update(requester);
    }

    @Override
    public void addFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
        udUserDao.update(requester);
        udUserDao.update(addressee);
    }

    @Override
    public void remove(@Nonnull UserEntity user) {
        udUserDao.delete(user);
    }
}
