package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VoidClubController {

    @FXML
    private TableView statistics;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn winsColumn;
    @FXML
    private TableColumn drawsColumn;
    @FXML
    private TableColumn losesColumn;
    @FXML
    private TableColumn pointsColumn;
    @FXML
    private TableColumn rankColumn;


    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }


}
