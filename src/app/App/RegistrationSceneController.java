package App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

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
    private void Registration() {
        Warning.setText("");
        String name = Name.getText();
        String password=Password.getText();
        String email=Email.getText();
        if(name.isEmpty()||password.isEmpty()) {
            Warning.setText("Vyplnťe všetky povinné polia!");
            return;
        }
        PlayerDatabase.Registration(name,password,email);
        Name.setText("");
        Password.setText("");
        Email.setText("");
        Warning.setText("Registrácia prebehla úspešne!");
    }
    @FXML
    private void ChangeSceneLogin() throws IOException {
        LoginSceneController.switchScene("/GUI/LoginScene.fxml");
    }
}
