package basement;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import java.util.UUID;


public class CassandraConnector {

    private CqlSession session;

    public void connect() {
        session = CqlSession.builder()
                .withLocalDatacenter("datacenter1")
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

    public void createTable(String tableName) {
        String query = "CREATE TABLE IF NOT EXISTS my_keyspace." + tableName +"(" +
                "    post_id UUID PRIMARY KEY," +
                "    title TEXT," +
                "    summary TEXT" +
                ");";
        session.execute(query);
    }

    public void insertPost(String title, String summary) {
        UUID uuid = UUID.randomUUID();
        String query = "INSERT INTO my_keyspace.posts (post_id, title, summary) VALUES (?, ?, ?)";

        PreparedStatement preparedStatement = session.prepare(query);

        session.execute(preparedStatement.bind(uuid, title, summary));
    }

}