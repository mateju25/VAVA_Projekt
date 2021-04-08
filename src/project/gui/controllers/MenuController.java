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
}
