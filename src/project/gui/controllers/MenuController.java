package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import project.gui.Main;

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


    public void initialize() {
        refreshTexts();
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
        settingsText.setText(Main.bundle.getString("settingsText"));
        tournamentText.setText(Main.bundle.getString("tournamentText"));
        voidClubText.setText(Main.bundle.getString("voidClubText"));
        profileText.setText(Main.bundle.getString("profileText"));
        logOutText.setText(Main.bundle.getString("logoutText"));
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
