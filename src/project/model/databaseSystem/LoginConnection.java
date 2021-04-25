package project.model.databaseSystem;

import javafx.scene.control.Button;

import javax.mail.*;
import javax.mail.internet.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginConnection {
    private  final static Logger LOGGER = Logger.getLogger(LoginConnection.class.getName());

    private static LoginConnection single_instance = null;
    private ChessPlayer activePlayer = null;
    private ArrayList<ChessPlayer> chessPlayerslist = null;


    private final String connectionUrl = "jdbc:sqlserver://fiit-vava.database.windows.net:1433;database=fiit-vava-dbs;user=matej.delincak@fiit-vava;password=28qpj2C5zXTDmRn45x9wlkj;";

    private LoginConnection() {}

    public static LoginConnection getInstance() {
        if (single_instance == null)
            single_instance = new LoginConnection();

        return single_instance;
    }

    public ChessPlayer getActivePlayer() {
        return activePlayer;
    }

    public ArrayList<ChessPlayer> getChessPlayerslist() {
        if (chessPlayerslist == null) {
            chessPlayerslist = new ArrayList<>();
            loadPlayers();
        }
        return chessPlayerslist;
    }

    public boolean existsUserName(String name, String email) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        boolean result = true;
        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? or email = ?;");
            statement.setString(1, name);
            statement.setString(2, email);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                result = false;
            }
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
        return result;
    }

    public boolean loginUser(String name, String password) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        boolean result = true;
        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("SELECT * FROM users WHERE username= ? and password= ?;");
            statement.setString(1, name);
            statement.setString(2,  hashPass(password));
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                result = false;
            } else {
                this.activePlayer = new ChessPlayer(resultSet.getString("username"),
                        password,
                        resultSet.getString("email"),
                        resultSet.getBoolean("administrator"),
                        resultSet.getInt("gamesPC"),
                        resultSet.getInt("gamesPlayer"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("draws"),
                        resultSet.getInt("loses"),
                        resultSet.getInt("board"),
                        resultSet.getInt("pieces"),
                        resultSet.getBoolean("tournament"));
            }
            connection.close();
        } catch (SQLException a) {
            result = false;
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
        return result;
    }

    public void saveUser() {
        PreparedStatement statement = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("UPDATE users SET gamesPC = ?, gamesPlayer = ?, wins = ?, draws = ?, loses = ?, board = ?, pieces = ?, tournament = ?, password = ? WHERE email= ?;");
            statement.setInt(1, activePlayer.getGamesVsPc());
            statement.setInt(2, activePlayer.getGamesVsPlayer());
            statement.setInt(3, activePlayer.getWins());
            statement.setInt(4, activePlayer.getDraws());
            statement.setInt(5, activePlayer.getLoses());
            statement.setInt(6, activePlayer.getFavouriteBoard());
            statement.setInt(7, activePlayer.getFavouritePieces());
            statement.setInt(8, activePlayer.isParticipant() ? 1 : 0);
            statement.setString(9, hashPass(activePlayer.getPassword()));
            statement.setString(10, activePlayer.getEmail());
            statement.executeUpdate();

            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }

    }

    public void loadPlayers() {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("SELECT * FROM users;");
            resultSet = statement.executeQuery();
            chessPlayerslist.clear();
            while (resultSet.next()) {
                this.chessPlayerslist.add(new ChessPlayer(resultSet.getString("username"),
                        "unknown",
                        resultSet.getString("email"),
                        resultSet.getBoolean("administrator"),
                        resultSet.getInt("gamesPC"),
                        resultSet.getInt("gamesPlayer"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("draws"),
                        resultSet.getInt("loses"),
                        resultSet.getInt("board"),
                        resultSet.getInt("pieces"),
                        resultSet.getBoolean("tournament")));
            }
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
    }

    public void registrationUser(String name, String password, String email) {
        PreparedStatement statement = null;

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            statement = connection.prepareStatement("INSERT INTO users VALUES (? , ? , ?, 0, 0, 0, 0, 0, 0, 1, 1, 0)");
            statement.setString(1, name);
            statement.setString(2, hashPass(password));
            statement.setString(3, email);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException a) {
            LOGGER.log(Level.SEVERE, "Nastala chyba v spojení s databázou v metóde: " + new Object(){}.getClass().getEnclosingMethod().getName());
        }
    }

    public void sendWelcomeEmail(String email, int pin) {
        String to = email;
        String from = "voidchess.fiit@gmail.com";
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("voidchess.fiit@gmail.com", "28qpj2C5zXTeruiyyBDHD45");

            }

        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Vitaj vo void chesse !");
            message.setText("Pre úspešné zaregistrovanie zadaj tento PIN do aplikácie: " + String.valueOf(pin));
            Transport.send(message);
        } catch (MessagingException mex) {
            LOGGER.log(Level.SEVERE, "Nastala chyba pri poslaní emailu");
        }
    }

    public String hashPass(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (int i = 0; i < encodedhash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedhash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}