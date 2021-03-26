package project.model.loginSystem;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.sql.*;
import java.util.Properties;

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
            mex.printStackTrace();
        }
    }
}