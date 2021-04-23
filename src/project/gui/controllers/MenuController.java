package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MenuController {

    @FXML
    private Text settingsText;
    @FXML
    private Text tournamentText;
    @FXML
    private Text profileText;
    @FXML
    private Text voidClubText;
    @FXML
    private Text logOutText;
    private ResourceBundle bundle=null;

    public void initialize() {
        Locale.setDefault(new Locale("sk"));
        bundle = ResourceBundle.getBundle("project/gui/resources/bundles/slovak");
        refreshTexts();
    }
    public void changeToSlovak() {
        Locale.setDefault(new Locale("sk"));
        bundle = ResourceBundle.getBundle("project/gui/resources/bundles/slovak");
        refreshTexts();
    }

    public  void changeToEnglish() {
        Locale.setDefault(new Locale("us"));
        bundle = ResourceBundle.getBundle("project/gui/resources/bundles/english");
        refreshTexts();
    }

    private void refreshTexts() {
        settingsText.setText(bundle.getString("settingsText"));
        tournamentText.setText(bundle.getString("tournamentText"));
        voidClubText.setText(bundle.getString("voidClubText"));
        profileText.setText(bundle.getString("profileText"));
        logOutText.setText(bundle.getString("logoutText"));
    }

    @FXML
    private void logOut() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/LoginScene.fxml");

    }
    @FXML
    private void changeSceneSingleplayer() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/SingleplayerScene.fxml");

    }
    @FXML
    private void changeSceneMultiplayer() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MultiplayerScene.fxml");

    }
    @FXML
    private void changeSceneTournament() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/TournamentScene.fxml");

    }
    @FXML
    private void changeSceneVoidClub() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/VoidClubScene.fxml");

    }
    @FXML
    private void changeSceneProfile() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/ProfileScene.fxml");

    }
    @FXML
    private void changeSceneSettings() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/SettingsScene.fxml");

    }
}
