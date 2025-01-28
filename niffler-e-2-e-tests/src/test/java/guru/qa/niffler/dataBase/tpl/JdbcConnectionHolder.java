package guru.qa.niffler.dataBase.tpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcConnectionHolder implements AutoCloseable {
    private final DataSource dataSource;
    private final Map<Long, Connection> treadConnections = new ConcurrentHashMap<>();

    public JdbcConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection connection() {
        return treadConnections.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> {
                    try {
                        return dataSource.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public void close() throws Exception {
        Optional.ofNullable(treadConnections.remove(Thread.currentThread().threadId()))
                .ifPresent(connection -> {
                    try {
                        connection.close();
                    } catch (SQLException e) {

                    }
                });
    }

    public void closeAllConnections() {
        treadConnections.values().forEach(connection -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        });
    }
}
