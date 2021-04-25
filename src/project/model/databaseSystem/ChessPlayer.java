package project.model.databaseSystem;

import javafx.scene.image.Image;

public class ChessPlayer {
    private final String name;
    private String password;
    private final String email;
    private int matches;
    private int gamesVsPc;
    private int gamesVsPlayer;
    private int wins;
    private int draws;
    private int loses;
    private double points;
    private Image photo;
    private boolean administrator;
    private boolean participant;

    public ChessPlayer(String name, String password, String email, int gamesVsPc, int gamesVsPlayer, int wins, int draws, int loses, boolean administrator, boolean participant) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.matches = (short) (gamesVsPc + gamesVsPlayer);
        this.gamesVsPc = gamesVsPc;
        this.gamesVsPlayer = gamesVsPlayer;
        this.wins = wins;
        this.draws = draws;
        this.loses = loses;
        this.points = wins + draws*0.5;
        this.photo = null;
        this.administrator = administrator;
        this.participant = participant;
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

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getGamesVsPc() {
        return gamesVsPc;
    }

    public void setGamesVsPc(int gamesVsPc) {
        this.gamesVsPc = gamesVsPc;
    }

    public int getGamesVsPlayer() {
        return gamesVsPlayer;
    }

    public void setGamesVsPlayer(int gamesVsPlayer) {
        this.gamesVsPlayer = gamesVsPlayer;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public double getPoints() {
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

    public void setPoints(double points) {
        this.points = points;
    }

    public boolean isParticipant() {
        return participant;
    }

    public void setParticipant(boolean participant) {
        this.participant = participant;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
}
