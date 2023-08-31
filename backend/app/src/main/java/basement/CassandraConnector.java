package basement;

import com.datastax.oss.driver.api.core.CqlSession;


public class CassandraConnector {

    private CqlSession session;

    public void connect() {
        session = CqlSession.builder()
                .withLocalDatacenter("datacenter1")
                .withKeyspace("my_keyspace")
                .build();
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }

    public void createKeyspace(
            String keyspaceName, String replicationStrategy, int replicationFactor) {

        String query = "CREATE KEYSPACE IF NOT EXISTS " +
                keyspaceName + " WITH replication = {" +
                "'class':'" + replicationStrategy +
                "','replication_factor':" + replicationFactor +
                "};";
        session.execute(query);
    }
}