package aq.koptev.servecies.regitraration;

import aq.koptev.model.User;
import aq.koptev.servecies.dbconnect.DBConnectURL;
import aq.koptev.servecies.dbconnect.DBConnector;
import aq.koptev.servecies.dbconnect.SQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBRegistrationService implements RegistrationService {

    private DBConnector connector;

    public DBRegistrationService() {
        connector = new SQLiteConnector();
    }

    @Override
    public boolean registerUser(User user) {
        String sql = "INSERT INTO Users (login, password) VALUES (?, ?)";
        try (Connection connection = connector.getConnection(DBConnectURL.CHAT_DB.getURL());
             PreparedStatement preparedStatement = connector.getPreparedStatement(connection, sql)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            connection.setAutoCommit(false);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
