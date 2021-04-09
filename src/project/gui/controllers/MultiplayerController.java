package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import project.gui.Main;

import java.io.IOException;

public class MultiplayerController {
    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }
    @FXML
    private void changeSceneGameBoard() throws IOException {

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Main.primaryStage.setX(bounds.getMinX());
        Main.primaryStage.setY(bounds.getMinY());
        Main.primaryStage.setWidth(bounds.getWidth());
        Main.primaryStage.setHeight(bounds.getHeight());
        Main.primaryStage.setMaximized(true);
        LoginSceneController.switchScene("/project/gui/views/GameBoard.fxml");
    }
}
