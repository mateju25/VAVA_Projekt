package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import project.gui.Main;
import project.model.databaseSystem.LoginConnection;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginSceneController {


    @FXML
    private TextField name;
    @FXML
    private PasswordField password;
    @FXML
    private Label warning;
    @FXML
    private Button regsitrationBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private Button loginBtn;
    public void initialize() {
        refreshTexts();
    }
    @FXML
    private void setCancelButton() {
        System.exit(0);
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        warning.setText("");
//        String name = this.name.getText();
//        String password= this.password.getText();
//
//        if(name.isEmpty()||password.isEmpty()) {
//            warning.setText("Vyplnťe všetky povinné polia!");
//            return;
//        }

        if (!LoginConnection.getInstance().loginUser("Morfex", "voidchess25")) {
            warning.setText("Nepodarilo sa prihlasit");
            return;
        }



        switchScene("/project/gui/views/MenuScene.fxml");
    }

    @FXML
    private void changeSceneRegistration() throws IOException {
        switchScene("/project/gui/views/RegistrationScene.fxml");

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
        loginBtn.setText(Main.bundle.getString("loginBtn"));
        regsitrationBtn.setText(Main.bundle.getString("registrationBtn"));
        exitBtn.setText(Main.bundle.getString("exitBtn"));
    }

    public static void switchScene(String s) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource(s));
        Scene scene = new Scene(root);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }
}

