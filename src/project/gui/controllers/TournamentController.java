package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TournamentController implements Initializable {
    @FXML
    private Label warning;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warning.setVisible(false);
    }
    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }


}
