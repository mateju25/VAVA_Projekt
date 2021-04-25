package project.model.databaseSystem;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//vytvorim na zaciatku programu len jeden ktory bude false a pocas behu programu to budem menit
public class Tournament {
    private  final static Logger LOGGER = Logger.getLogger(LoginConnection.class.getName());

    private int format;
    private boolean active = false;
    private Map<String, String> mapOfParticipants = new HashMap<>();
    private static Tournament single_instance = null;
    private final String connectionUrl = "jdbc:sqlserver://fiit-vava.database.windows.net:1433;database=fiit-vava-dbs;user=matej.delincak@fiit-vava;password=28qpj2C5zXTDmRn45x9wlkj;";

    // vrat lokalnu mapu hracov
    public Map<String, String> getMapOfParticipants() {
        return mapOfParticipants;
    }

    // setni lokalnu mapu hracov
    public void setMapOfParticipants(Map<String, String> mapOfParticipants) {
        this.mapOfParticipants = mapOfParticipants;
    }
    public static Tournament getInstance() {
        if (single_instance == null)
            single_instance = new Tournament();

        return single_instance;
    }

    private Tournament() {

        mapOfParticipants = loadMapOfParticipantsFromDatabase();
    }

    // nacitaj mapu hracov z databazy
    public Map<String, String> loadMapOfParticipantsFromDatabase() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Map<String, String> newMap = new HashMap<>();

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT * FROM tournament");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                newMap.put(id, name);
            }
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou pri multiplayeri v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
        this.active = newMap.containsKey("1");
        return newMap;
    }

    public int loadType() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        int result = 0;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("SELECT * FROM tournament_info");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou pri turnaji v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
        return result;
    }

    public void setType() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("DELETE FROM tournament_info;");
            statement.executeUpdate();
            statement = connection.prepareStatement("INSERT INTO tournament_info VALUES (?);");
            statement.setInt(1, this.format);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou pri turnaji v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
    }

    // uloz aktualnu lokalnu mapu hracov do databazy
    public void setMapOfParticipantsToDatabase() {
        PreparedStatement statement = null;


        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("DELETE FROM tournament");
            statement.executeUpdate();
            statement = connection.prepareStatement("INSERT INTO tournament VALUES (?, ?);");
            for (String key :
                    mapOfParticipants.keySet()) {
                statement.setString(1, key);
                statement.setString(2, mapOfParticipants.get(key));
                statement.executeUpdate();
            }
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou pri multiplayeri v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
        setType();
    }

    public void deleteTournament() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("DELETE FROM tournament_info;");
            statement.executeUpdate();
            statement = connection.prepareStatement("DELETE FROM tournament;");
            statement.executeUpdate();
            statement = connection.prepareStatement("UPDATE users SET tournament = 0;");
            statement.executeUpdate();
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou pri turnaji v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }
}
