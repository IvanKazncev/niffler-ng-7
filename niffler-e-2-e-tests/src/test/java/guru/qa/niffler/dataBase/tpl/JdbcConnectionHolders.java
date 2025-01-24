package guru.qa.niffler.dataBase.tpl;

import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcConnectionHolders implements AutoCloseable{

    private final List<JdbcConnectionHolder> holders;

    public JdbcConnectionHolders(List<JdbcConnectionHolder> holders) {
        this.holders = holders;
    }


    @SneakyThrows
    @Override
    public void close() throws Exception {
        holders.forEach(jdbcConnectionHolder -> {
            try {
                jdbcConnectionHolder.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
