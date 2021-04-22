package project.model.databaseSystem;

import project.model.GameParticipant;

import java.sql.*;
import java.time.LocalTime;

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

    public void createNewGame(boolean black, LocalTime time) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT MAX(id) FROM multiplayer;");
            resultSet = statement.executeQuery();


            if (resultSet.next()) {
                this.id = resultSet.getInt(1) + 1;
                statement = connection.prepareStatement("INSERT INTO multiplayer VALUES (? , '', ?, ?, ?)");
                statement.setInt(1, this.id);
                statement.setInt(2, black ? 1 : 0);
                statement.setInt(3, time.toSecondOfDay());
                statement.setInt(4, time.toSecondOfDay());
                statement.executeUpdate();

            }
            connection.close();
        } catch (SQLException a) {}
    }

    public void setTimers(LocalTime ownTime, LocalTime otherTime) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("UPDATE multiplayer SET ownerTime = ?, secondTime = ? WHERE id = ?");
            statement.setInt(3, this.id);
            statement.setInt(1, ownTime.toSecondOfDay());
            statement.setInt(2, otherTime.toSecondOfDay());
            statement.executeUpdate();
            connection.close();
        } catch (SQLException a) {}
    }

    public boolean getColor() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int result = 0;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT isOwnerBlack FROM multiplayer WHERE id = ?;");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();


            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            connection.close();
        } catch (SQLException a) {}
        return result == 1;
    }

    public LocalTime[] getTimes() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        LocalTime[] result = new LocalTime[2];

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT ownerTime, secondTIme FROM multiplayer WHERE id = ?;");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();


            if (resultSet.next()) {
                result[0] = LocalTime.of(0, resultSet.getInt(1)/60, resultSet.getInt(1)%60);
                result[1] = LocalTime.of(0, resultSet.getInt(2)/60, resultSet.getInt(2)%60);
            }
            connection.close();
        } catch (SQLException a) {}
        return result;
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
