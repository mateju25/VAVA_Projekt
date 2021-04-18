package project.model.databaseSystem;

import project.model.GameParticipant;

import java.sql.*;

public class MultiplayerConnection implements GameParticipant {
    private static MultiplayerConnection single_instance = null;
    private int id = 0;
    private final String connectionUrl = "jdbc:sqlserver://fiit-vava.database.windows.net:1433;database=fiit-vava-dbs;user=matej.delincak@fiit-vava;password=28qpj2C5zXTDmRn45x9wlkj;";

    private MultiplayerConnection() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static MultiplayerConnection getInstance() {
        if (single_instance == null)
            single_instance = new MultiplayerConnection();

        return single_instance;
    }

    public void createNewGame(boolean black) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT MAX(id) FROM multiplayer;");
            resultSet = statement.executeQuery();


            if (resultSet.next()) {
                this.id = resultSet.getInt(1) + 1;
                statement = connection.prepareStatement("INSERT INTO multiplayer VALUES (? , '', ?)");
                statement.setInt(1, this.id);
                statement.setInt(2, black ? 1 : 0);
                statement.executeUpdate();

            }
            connection.close();
        } catch (SQLException a) {}
    }

    public void makeMove(String move) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("UPDATE multiplayer SET lastmove = ? WHERE id = ?");
            statement.setInt(2, this.id);
            statement.setString(1, move);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException a) {}
    }

    public String getLastMove() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String result = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT lastmove FROM multiplayer WHERE id = ?;");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();


            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
            connection.close();
        } catch (SQLException a) {}
        return result;
    }

}
