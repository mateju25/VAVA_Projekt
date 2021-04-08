package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;


import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import project.gui.Main;
import project.model.loginSystem.ChessPlayer;
import project.model.loginSystem.PlayerDatabase;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Main.primaryStage.setResizable(true);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Main.primaryStage.setX(bounds.getMinX());
        Main.primaryStage.setY(bounds.getMinY());
        Main.primaryStage.setWidth(bounds.getWidth());
        Main.primaryStage.setHeight(bounds.getHeight());

        Main.primaryStage.setMaximized(true);
        switchScene("/project/gui/views/GameBoard.fxml");


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

