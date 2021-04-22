package project.model.databaseSystem;

import javafx.scene.image.Image;

public class ChessPlayer {
    private final String name;
    private String password;
    private final String email;
    private short matches;
    private short gamesVsPc;
    private short gamesVsPlayer;
    private short wins;
    private short draws;
    private short loses;
    private short points;
    private Image photo;
    private boolean administrator;

    public ChessPlayer(String name, String password, String email, boolean administrator) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.matches = 0;
        this.gamesVsPc = 0;
        this.gamesVsPlayer = 0;
        this.wins = 0;
        this.draws = 0;
        this.loses = 0;
        this.points = 0;
        this.photo = new Image("/project/gui/resources/pictures/Profile-Avatar-PNG.png");
        this.administrator = administrator;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public short getMatches() {
        return matches;
    }

    public void setMatches(short matches) {
        this.matches = matches;
    }

    public short getGamesVsPc() {
        return gamesVsPc;
    }

    public void setGamesVsPc(short gamesVsPc) {
        this.gamesVsPc = gamesVsPc;
    }

    public short getGamesVsPlayer() {
        return gamesVsPlayer;
    }

    public void setGamesVsPlayer(short gamesVsPlayer) {
        this.gamesVsPlayer = gamesVsPlayer;
    }

    public short getWins() {
        return wins;
    }

    public void setWins(short wins) {
        this.wins = wins;
    }

    public short getDraws() {
        return draws;
    }

    public void setDraws(short draws) {
        this.draws = draws;
    }

    public short getLoses() {
        return loses;
    }

    public void setLoses(short loses) {
        this.loses = loses;
    }

    public short getPoints() {
        return points;
    }

    public void setPoints(short points) {
        this.points = points;
    }

    public Image getPhoto() {
        return photo;
    }

    public boolean isAdministrator() {
        return administrator;
    }


    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
}
