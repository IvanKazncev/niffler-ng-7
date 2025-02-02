package guru.qa.niffler.dataBase.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.dbConnection.DataBases;
import guru.qa.niffler.dataBase.entity.*;
import guru.qa.niffler.dataBase.impl.*;
import guru.qa.niffler.dataBase.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.dataBase.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.dataBase.tpl.DataSources;
import guru.qa.niffler.dataBase.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.dataBase.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.AuthorityJson;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.UUID;

import static guru.qa.niffler.dataBase.dbConnection.DataBases.*;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();


    private final TransactionTemplate transactionTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJDBCUrl())
            )
    );

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.authJDBCUrl()
    );

    private final XaTransactionTemplate xaJdbcTxTemplate = new XaTransactionTemplate(
            CFG.authJDBCUrl(),
            CFG.userdataJDBCUrl()
    );

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            DataSources.dataSource(CFG.authJDBCUrl())),
                    new JdbcTransactionManager(
                            DataSources.dataSource(CFG.userdataJDBCUrl())))
    );


    public Record createUser(AuthUserJson authUser, UserJson userJson) {
        return xaTransaction(1,
                new DataBases.XaFunction<>(
                        connection -> {
                            AuthUserEntity auth = AuthUserEntity.fromJson(
                                    authUser
                            );
                            return AuthUserJson.fromEntity(
                                    new AuthUserDaoJdbc().createUser(auth)
                            );
                        }, CFG.authJDBCUrl()),
                new DataBases.XaFunction<>(
                        connection -> {
                            UserEntity user = UserEntity.fromJson(
                                    userJson
                            );
                            return UserJson.fromEntity(
                                    new UseDataDaoJdbc().createUser(user)
                            );
                        }, CFG.userdataJDBCUrl())

        );
    }

    public UserJson createUserSpringJdbc(UserJson userJson) throws Exception {
        xaJdbcTxTemplate.transaction(1,
                () -> {
                    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                    AuthUserEntity authUserEntity = new AuthUserEntity();
//                    authUserEntity.setId(UUID.randomUUID());
                    authUserEntity.setUsername(userJson.username());
                    authUserEntity.setPassword(passwordEncoder.encode("123"));
                    authUserEntity.setEnabled(true);
                    authUserEntity.setAccountNonExpired(true);
                    authUserEntity.setAccountNonLocked(true);
                    authUserEntity.setCredentialsNonExpired(true);
                    authUserEntity.setAuthorities(Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity authorityEntity = new AuthorityEntity();
                                authorityEntity.setId(UUID.randomUUID());
                                authorityEntity.setAuthority(e);
                                authorityEntity.setUser(authUserEntity);
                                return authorityEntity;
                            }
                    ).toList());

                    AuthUserEntity createdAuthUser = new AuthUserRepositorySpringJdbc().createUser(authUserEntity);
                    return createdAuthUser;
                });

        return UserJson.fromEntity(
                new UseDataDaoSpringJdbc()
                        .createUser(
                                UserEntity.fromJson(userJson)
                        )
        );
    }

    public UserJson createUserSpringJdbcChained(AuthUserJson authUserJson, UserJson userJson) throws Exception {
        txTemplate.execute(
                transactionStatus -> {
                    transactionStatus.isCompleted();
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
                    AuthUserEntity createdAuthUser = new AuthUserDaoJdbc()
                            .createUser(authUserEntity);
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(createdAuthUser.getId());
                    authorityEntity.setAuthority(Authority.write);

                    new AuthorityDaoJdbc().createUser(authorityEntity);
                    return null;
                });

        return UserJson.fromEntity(
                new UseDataDaoJdbc()
                        .createUser(
                                UserEntity.fromJson(userJson)
                        )
        );
    }

}
