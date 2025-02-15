package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.authJdbcUrl();

  @Nonnull
  @Override
  public void create(AuthorityEntity... authority) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
      for (AuthorityEntity a : authority) {
        ps.setObject(1, a.getUser().getId());
        ps.setString(2, a.getAuthority().name());
        ps.addBatch();
        ps.clearParameters();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  @Nonnull
  @Override
  public List<AuthorityEntity> findAll() {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM authority")) {
      ps.execute();
      List<AuthorityEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          result.add(AuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  @Nonnull
  @Override
  public List<AuthorityEntity> findAllByUserId(UUID userId) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM authority where user_id = ?")) {
      ps.setObject(1, userId);
      ps.execute();
      List<AuthorityEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          result.add(AuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(AuthorityEntity authorityEntity) {
    try (PreparedStatement deletePs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "DELETE FROM authority WHERE user_id = ?")) {
      deletePs.setObject(1, authorityEntity.getUser().getId());
      deletePs.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  @Nonnull
  @NotNull
  private static AuthorityEntity map(ResultSet rs) throws SQLException {
    AuthorityEntity authority = new AuthorityEntity();
    authority.setId(rs.getObject("id", UUID.class));
    AuthUserEntity userEntity = new AuthUserEntity();
    userEntity.setId(rs.getObject("user_id", UUID.class));
    authority.setUser(userEntity);
    authority.setAuthority(Authority.valueOf(rs.getString("authority")));

    return authority;
  }
}
