package project.model.loginSystem;

import java.sql.*;

public class PlayerDatabase {
    private static PlayerDatabase single_instance = null;
    private ChessPlayer activePlayer = null;

    private final String connectionUrl = "jdbc:sqlserver://fiit-vava.database.windows.net:1433;database=fiit-vava-dbs;user=matej.delincak@fiit-vava;password=28qpj2C5zXTDmRn45x9wlkj;";

    private PlayerDatabase() {

    }

    public static PlayerDatabase getInstance() {
        if (single_instance == null)
            single_instance = new PlayerDatabase();

        return single_instance;
    }

    public ChessPlayer getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(ChessPlayer activePlayer) {
        this.activePlayer = activePlayer;
    }

    public boolean existsUserName(String name, String email) {
        ResultSet resultSet = null;
        Statement statement = null;

        boolean result = true;
        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE username='" + name +"' or email='" + email + "';");
            if (resultSet.next()) {
                result = true;
            } else {
                result = false;
            }
            connection.close();
        } catch (SQLException a) {
            result = true;
        }
        return result;
    }

    public boolean loginUser(String name, String password) {
        ResultSet resultSet = null;
        Statement statement = null;

        boolean result = true;
        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE username='" + name +"' and password='" + password + "';");
            if (resultSet.next()) {
                result = true;
            } else {
                result = false;
            }
            connection.close();
        } catch (SQLException a) {
            result = true;
        }
        return result;
    }

    public void registrationUser(String name, String password, String email) {
        Statement statement = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users VALUES ('" + name +"', '" + password + "', '" + email + "')");
            connection.close();
        } catch (SQLException a) {}
    }
}