package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import project.model.loginSystem.PlayerDatabase;

import java.io.IOException;
import java.sql.SQLException;

public class RegistrationSceneController {
    @FXML
    private TextField Name;
    @FXML
    private TextField Password;
    @FXML
    private TextField Email;
    @FXML
    private Label Warning;
    @FXML
    private void registration() {
        Warning.setText("");
        String name = Name.getText();
        String password=Password.getText();
        String email=Email.getText();
        if(name.isEmpty()||password.isEmpty()) {
            Warning.setText("Vyplnťe všetky povinné polia!");
            return;
        }
        try {
            PlayerDatabase.registration(name,password,email);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Name.setText("");
        Password.setText("");
        Email.setText("");
        Warning.setText("Registrácia prebehla úspešne!");
    }
    @FXML
    private void changeSceneLogin() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/LoginScene.fxml");
    }
}
