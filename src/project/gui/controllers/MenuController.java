package project.gui.controllers;

import javafx.fxml.FXML;

import java.io.IOException;

public class MenuController {
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
