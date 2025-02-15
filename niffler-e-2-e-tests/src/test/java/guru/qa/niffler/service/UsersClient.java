package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import javax.annotation.Nonnull;

public interface UsersClient {
  @Nonnull
  static UsersClient getInstance() {
    return "api".equals(System.getProperty("client.impl"))
            ? new UsersApiClient()
            : new UsersDbClient();
  }
  UserJson createUser(String username, String password);

  void addIncomeInvitation(UserJson targetUser, int count);

  void addOutcomeInvitation(UserJson targetUser, int count);

  void addFriend(UserJson targetUser, int count);
}
