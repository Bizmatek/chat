package server;

import lombok.extern.log4j.Log4j2;
import server.dao.ConfigurationFactory;
import server.dao.UsersQueriesConfig;

import java.sql.*;

@Log4j2
public class DBAuthService implements AuthService {
    private Connection connection;
    private Statement statement;
    private static final UsersQueriesConfig QUERIES_CONFIG = ConfigurationFactory.getUsersQueriesConfig();

    public DBAuthService() {
        String dbLocation = getClass().getResource("/users.db").toString();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e){
            log.error("Unable to connect database {}", dbLocation);
        }
    }

    @Override
    public String getLoginByLoginAndPassword(String login, String password) {
        String query = String.format(QUERIES_CONFIG.selectUserByName(), login);
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.next()){
                String resultPassword = rs.getObject(3).toString();
                if(password.equals(resultPassword)){
                    return rs.getObject(4).toString();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        String query = String.format(QUERIES_CONFIG.insertUser(), login, password, nickname);

        try {
            statement = connection.createStatement();

            log.info("Executing query: {}", query);
            int count = statement.executeUpdate(query);

            if (count == 0) {
                log.warn("Unable to update data");
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
