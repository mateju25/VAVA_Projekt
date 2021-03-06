package project.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import project.gui.Main;
import project.model.databaseSystem.LoginConnection;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationSceneController {
    @FXML
    private TextField name;
    @FXML
    private TextField password;
    @FXML
    private TextField email;
    @FXML
    private TextField textPin;
    @FXML
    private Label warning;
    @FXML
    private Button registrateBtn;
    @FXML
    private Button sendPinBtn;
    @FXML
    private Button backBtn;
    private int generatedPin = 0;

    public void initialize() {
        sendPinBtn.setVisible(false);
        textPin.setVisible(false);
    }

    @FXML
    private void registration() {
        warning.setText("");
        String name = this.name.getText();
        String password= this.password.getText();
        String email= this.email.getText();
        String pin= this.textPin.getText();

        if(name.isEmpty() || password.isEmpty() /*|| pin.isEmpty()*/) {
            warning.setText(Main.bundle.getString("important"));
            return;
        }

        if(password.length()<6){
            warning.setText(Main.bundle.getString("shortPas"));
            return;
        }

        if(name.length()>20){
            warning.setText(Main.bundle.getString("longPas"));
            return;
        }

        Pattern p = Pattern.compile(".*@.*[.].*");
        Matcher m = p.matcher(email);
        if (!m.matches()) {
            warning.setText(Main.bundle.getString("badEmail"));
            return;
        }
        if (LoginConnection.getInstance().existsUserName(name, email)) {
            warning.setText(Main.bundle.getString("userDoNotExists"));
            return;
        }
//        if (!(String.valueOf(generatedPin).equals(pin)))  {
//            warning.setText("Nespr??vny PIN!");
//            return;
//        }

        LoginConnection.getInstance().registrationUser(name,password,email);

        this.name.setText("");
        this.password.setText("");
        this.email.setText("");
        warning.setText(Main.bundle.getString("registrationGood"));
    }
    @FXML
    private void changeSceneLogin() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/LoginScene.fxml");
    }
    public void changeToSlovak() {
        warning.setText("");
        Locale.setDefault(new Locale("sk"));
        Main.bundle = ResourceBundle.getBundle("project/gui/resources/bundles/slovak");
        refreshTexts();
    }

    public  void changeToEnglish() {
        warning.setText("");
        Locale.setDefault(new Locale("us"));
        Main.bundle = ResourceBundle.getBundle("project/gui/resources/bundles/english");
        refreshTexts();
    }


    private void refreshTexts() {
        name.setPromptText("????" + Main.bundle.getString("nameField"));
        password.setPromptText("\uD83D\uDD12"+Main.bundle.getString("passwordField"));
        registrateBtn.setText(Main.bundle.getString("registrateBtn"));
        sendPinBtn.setText(Main.bundle.getString("sendPinBtn"));
        backBtn.setText(Main.bundle.getString("backBtn"));
    }
    public void sendVerificationMail(ActionEvent actionEvent) {
        warning.setText("");
        Random rand = new Random();
        generatedPin = rand.nextInt(1000) * 9 + 1;
        Pattern p = Pattern.compile(".*@.*[.].*");
        Matcher m = p.matcher(email.getText());
        if (!m.matches()) {
            warning.setText("Email v zlom tvare!");
            return;
        }
        LoginConnection.getInstance().sendWelcomeEmail(email.getText(), generatedPin);
    }
}
