package guru.qa.niffler.dataBase.tpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

public class JdbcTransactionTemplate {

    private final JdbcConnectionHolder holder;

    public JdbcTransactionTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public JdbcTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public  <T> T transaction(Supplier<T> action, int transactionLevel) throws Exception {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setTransactionIsolation(transactionLevel);
            connection.setAutoCommit(false);
            T result;
            result = action.get();
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (SQLException e) {
            if (connection != null){
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }finally {
            if (closeAfterAction.get()){
                holder.close();
            }
        }
    }
    public  <T> T transaction(Supplier<T> action) throws Exception {
        return transaction(action,1);
    }
}
