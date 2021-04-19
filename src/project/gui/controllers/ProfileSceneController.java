package project.gui.controllers;

import javafx.fxml.FXML;


import java.io.IOException;


public class ProfileSceneController {




    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }


}