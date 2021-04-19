package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;


import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import project.gui.Main;

import java.io.IOException;

public class LoginSceneController {
    @FXML
    private TextField name;
    @FXML
    private PasswordField password;
    @FXML
    private Label warning;


    @FXML
    private void setCancelButton() {
        System.exit(0);
    }

    @FXML
    private void changeSceneMenu() throws IOException {
//        warning.setText("");
//        String name = this.name.getText();
//        String password= this.password.getText();
//
//        if(name.isEmpty()||password.isEmpty()) {
//            warning.setText("Vyplnťe všetky povinné polia!");
//            return;
//        }
//
//        if (!PlayerDatabase.getInstance().loginUser(name, password)) {
//            warning.setText("Nepodarilo sa prihlasit");
//            return;
//        }



        switchScene("/project/gui/views/MenuScene.fxml");
    }

    @FXML
    private void changeSceneRegistration() throws IOException {
        switchScene("/project/gui/views/RegistrationScene.fxml");

    }


    public static void switchScene(String s) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource(s));
        Scene scene = new Scene(root);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }
}

