package project.model.loginSystem;

import java.sql.*;
import java.util.ArrayList;

public class PlayerDatabase {
    private static ArrayList<ChessPlayer> playersList = new ArrayList<>();

    public static boolean registration(String name, String password, String email) throws SQLException {
        for (ChessPlayer pl :
                playersList) {
            if (pl.getEmail().equals(email))
                return false;
        }
        try (Connection conn = DriverManager
                .getConnection("jdbc:mysql://sql11.freemysqlhosting.net:3306",
                        "sql11401456", "NHp29lP6fb")) {

            PreparedStatement selectStatement = conn.prepareStatement("select * from users");
            ResultSet rs = selectStatement.executeQuery();

            while (rs.next()) { // will traverse through all rows
                int temp;
            }

        }
        playersList.add(new ChessPlayer(name,password,email));
        return true;
    }

    public static ArrayList<ChessPlayer> getZakaznicilist() {
        return playersList;
    }
}