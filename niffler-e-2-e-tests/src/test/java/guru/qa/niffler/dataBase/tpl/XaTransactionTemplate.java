package guru.qa.niffler.dataBase.tpl;

import com.atomikos.icatch.jta.UserTransactionImp;
import guru.qa.niffler.dataBase.dbConnection.DataBases;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class XaTransactionTemplate {

    private final JdbcConnectionHolders holders;

    public XaTransactionTemplate(String...jdbcUrl) {
        this.holders = Connections.holders(jdbcUrl);
    }
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public XaTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public  <T> T transaction(int transactionLevel, Supplier<T>... actions) throws Exception {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            T result = null;
            for (Supplier<T> action : actions) {
                try {
                    result = action.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            ut.commit();
            return result;
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            if (closeAfterAction.get()) {
                holders.close();
            }
        }
    }
}
