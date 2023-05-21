package data;

import java.sql.*;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection() throws SQLException {
        String connectionString = "jdbc:sqlserver://" + dbHost + ":" + dbPort + ";databaseName=" + dbName +
                ";trustServerCertificate=true";

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void signUpUser(String username, String password) throws SQLException {
        String insert =
                "INSERT INTO " + Constants.USER_TABLE + "(" + Constants.USER_USERNAME + "," + Constants.USER_PASSWORD + ")" + "VALUES(?,?)";

        String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, generatedSecuredPasswordHash);
        preparedStatement.executeUpdate();
    }

    public ResultSet getUser(String username) throws SQLException {
        ResultSet resultSet;

        String select = "SELECT * FROM " + Constants.USER_TABLE + " WHERE " + Constants.USER_USERNAME + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);
        preparedStatement.setString(1, username);

        resultSet = preparedStatement.executeQuery();

        return resultSet;
    }

    public void insertData(int id, String data, String domain, String username) throws SQLException {
        String insert =
                "INSERT INTO " + Constants.DATA_TABLE + "(" + Constants.USER_ID + "," + Constants.DATA_PASSWORD + "," + Constants.DATA_DOMAIN + "," + Constants.DATA_USERNAME + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, data);
        preparedStatement.setString(3, domain);
        preparedStatement.setString(4, username);
        preparedStatement.executeUpdate();
    }

    public ResultSet getData(int id) throws SQLException {
        ResultSet result;
        String select = "SELECT * FROM " + Constants.DATA_TABLE + " WHERE " + Constants.USER_ID + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);
        preparedStatement.setInt(1, id);

        result = preparedStatement.executeQuery();

        return result;
    }
}
