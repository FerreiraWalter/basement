package basement;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

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

    public void insertPost(String title, String summary, String body) {
        UUID uuid = UUID.randomUUID();
        String query = "INSERT INTO my_keyspace.posts (post_id, title, summary, body) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = session.prepare(query);

        session.execute(preparedStatement.bind(uuid, title, summary, body));
    }

    public Post getPostById(UUID postId) {
        String query = "SELECT * FROM my_keyspace.posts WHERE post_id = ?";

        PreparedStatement preparedStatement = session.prepare(query);

        BoundStatement boundStatement = preparedStatement.bind(postId);

        ResultSet resultSet = session.execute(boundStatement);

        return (Post) resultSet.one();
    }

    public void deletePostById(UUID postId) {
        String query = "DELETE FROM my_keyspace.posts WHERE post_id = ?";

        PreparedStatement preparedStatement = session.prepare(query);

        BoundStatement boundStatement = preparedStatement.bind(postId);

        session.execute(boundStatement);
    }

    public void updatePostById(UUID postId, String newTitle, String newSummary) {
        String query = "UPDATE my_keyspace.posts SET title = ?, summary = ? WHERE post_id = ?";

        PreparedStatement preparedStatement = session.prepare(query);

        BoundStatement boundStatement = preparedStatement.bind(newTitle, newSummary, postId);

        session.execute(boundStatement);
    }
}
