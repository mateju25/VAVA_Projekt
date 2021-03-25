package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import project.gui.Main;

import java.io.IOException;

public class LoginSceneController {
    @FXML
    private TextField Name;
    @FXML
    private TextField Password;
    @FXML
    private Label Warning;


    @FXML
    private void setCancelButton() {
        System.exit(0);

    }

    @FXML
    private void ChangeSceneMenu() throws IOException {
        switchScene("/project/gui/views/GameBoard.fxml");

    }

    @FXML
    private void ChangeSceneRegistration() throws IOException {
        switchScene("/project/gui/views/RegistrationScene.fxml");

    }


    public static void switchScene(String s) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource(s));
        Scene scene = new Scene(root);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }
}

