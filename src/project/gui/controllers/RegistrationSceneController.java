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

    @FXML
    private void registration() {
        warning.setText("");
        String name = this.name.getText();
        String password= this.password.getText();
        String email= this.email.getText();
        String pin= this.textPin.getText();

        if(name.isEmpty() || password.isEmpty() || pin.isEmpty()) {
            warning.setText("Vyplnťe všetky povinné polia!");
            return;
        }

        if(password.length()<6){
            warning.setText("Príliš krátke heslo - aspoň 6 znakov!");
            return;
        }

        if(name.length()>20){
            warning.setText("Príliš dlhé meno - najviac 20 znakov!");
            return;
        }

        Pattern p = Pattern.compile(".*@.*[.].*");
        Matcher m = p.matcher(email);
        if (!m.matches()) {
            warning.setText("Email v zlom tvare!");
            return;
        }
        if (LoginConnection.getInstance().existsUserName(name, email)) {
            warning.setText("Daný užívateľ už existuje!");
            return;
        }
//        if (!(String.valueOf(generatedPin).equals(pin)))  {
//            warning.setText("Nesprávny PIN!");
//            return;
//        }

        LoginConnection.getInstance().registrationUser(name,password,email);

        this.name.setText("");
        this.password.setText("");
        this.email.setText("");
        warning.setText("Registrácia prebehla úspešne!");
    }
    @FXML
    private void changeSceneLogin() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/LoginScene.fxml");
    }
    public void changeToSlovak() {
        Locale.setDefault(new Locale("sk"));
        Main.bundle = ResourceBundle.getBundle("project/gui/resources/bundles/slovak");
        refreshTexts();
    }

    public  void changeToEnglish() {
        Locale.setDefault(new Locale("us"));
        Main.bundle = ResourceBundle.getBundle("project/gui/resources/bundles/english");
        refreshTexts();
    }


    private void refreshTexts() {
        name.setPromptText("👤" + Main.bundle.getString("nameField"));
        password.setPromptText("\uD83D\uDD12"+Main.bundle.getString("passwordField"));
        registrateBtn.setText(Main.bundle.getString("registrateBtn"));
        sendPinBtn.setText(Main.bundle.getString("sendPinBtn"));
        backBtn.setText(Main.bundle.getString("backBtn"));
    }
    public void sendVerificationMail(ActionEvent actionEvent) {
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
