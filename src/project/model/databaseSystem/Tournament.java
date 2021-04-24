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
    private final String connectionUrl = "jdbc:sqlserver://fiit-vava.database.windows.net:1433;database=fiit-vava-dbs;user=matej.delincak@fiit-vava;password=28qpj2C5zXTDmRn45x9wlkj;";

    // vrat lokalnu mapu hracov
    public Map<String, String> getMapOfParticipants() {
        return mapOfParticipants;
    }

    // setni lokalnu mapu hracov
    public void setMapOfParticipants(Map<String, String> mapOfParticipants) {
        this.mapOfParticipants = mapOfParticipants;
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
        return newMap;
    }

    // uloz aktualnu lokalnu mapu hracov do databazy
    public void setMapOfParticipantsToDatabase() {
        PreparedStatement statement = null;

        Map<String, String> local = new HashMap<>();
        Map<String, String> database = loadMapOfParticipantsFromDatabase();

        for (String key :
                mapOfParticipants.keySet()) {
            if (!database.containsKey(key)) {
                local.put(key, mapOfParticipants.get(key));
            }
        }


        try {
            Connection connection = DriverManager.getConnection(connectionUrl);

            statement = connection.prepareStatement("INSERT INTO tournament VALUES (?, ?);");
            for (String key :
                    local.keySet()) {
                statement.setString(1, key);
                statement.setString(2, local.get(key));
                statement.executeUpdate();
            }
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou pri multiplayeri v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
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
