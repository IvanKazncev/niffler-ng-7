package guru.qa.niffler.dataBase.tpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    private Connections(){

    }
    private static final Map<String,JdbcConnectionHolder> holders = new ConcurrentHashMap<>();

    public  static JdbcConnectionHolder holder(String jdbcUrl){
        return holders.computeIfAbsent(
                jdbcUrl,
                key -> new JdbcConnectionHolder(
                        DataSources.dataSource(jdbcUrl)
                )
        );
    }
    public static void closeAllConnections() {
        holders.values().forEach(JdbcConnectionHolder::closeAllConnections);
    }


    public  static JdbcConnectionHolders holders(String...jdbcUrl) {
        List<JdbcConnectionHolder> holders = new ArrayList<>();
        for (String url : jdbcUrl) {
            holders.add(holder(url));
        }
        return new JdbcConnectionHolders(holders);
    }
}
