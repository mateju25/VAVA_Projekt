package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import project.model.loginSystem.PlayerDatabase;

import java.io.IOException;
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
    private Label warning;
    @FXML
    private void registration() {
        warning.setText("");
        String name = this.name.getText();
        String password= this.password.getText();
        String email= this.email.getText();

        if(name.isEmpty()||password.isEmpty()) {
            warning.setText("Vyplnťe všetky povinné polia!");
            return;
        }

        Pattern p = Pattern.compile(".*@.*[.].*");
        Matcher m = p.matcher(email);
        if (!m.matches()) {
            warning.setText("Email v zlom tvare!");
            return;
        }
        if (PlayerDatabase.getInstance().existsUserName(name, email)) {
            warning.setText("Daný užívateľ už existuje!");
            return;
        }
        PlayerDatabase.getInstance().registrationUser(name,password,email);

        this.name.setText("");
        this.password.setText("");
        this.email.setText("");
        warning.setText("Registrácia prebehla úspešne!");
    }
    @FXML
    private void changeSceneLogin() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/LoginScene.fxml");
    }
}
