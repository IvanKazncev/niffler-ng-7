package guru.qa.niffler.helpers.jupiter.extension;


import guru.qa.niffler.data.jdbc.Connections;

public class DataBasesExtension implements SuiteExtension{

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
