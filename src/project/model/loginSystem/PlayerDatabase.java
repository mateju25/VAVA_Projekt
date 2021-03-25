package project.model.loginSystem;

import java.util.ArrayList;

public class PlayerDatabase {

    public static ArrayList<ChessPlayer> PlayersList = new ArrayList<>();
    public static void Registration(String Name, String Password, String Email) {
        PlayersList.add(new ChessPlayer(Name,Password,Email));
    }

    public static ArrayList<ChessPlayer> getZakaznicilist() {
        return PlayersList;
    }
}