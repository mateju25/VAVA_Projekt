package project.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import project.model.databaseSystem.ChessPlayer;
import project.model.databaseSystem.LoginConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VoidClubController implements Initializable{

    @FXML
    private TableView<ChessPlayer> statistics;
    @FXML
    private TableColumn<ChessPlayer,String> nameColumn;
    @FXML
    private TableColumn<ChessPlayer,Short> winsColumn;
    @FXML
    private TableColumn<ChessPlayer,Short> drawsColumn;
    @FXML
    private TableColumn<ChessPlayer,Short> losesColumn;
    @FXML
    private TableColumn<ChessPlayer,Short> pointsColumn;
    @FXML
    private TableColumn<ChessPlayer,Short> matchesColumn;

    private final ObservableList<ChessPlayer> allPlayers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        matchesColumn.setCellValueFactory(new PropertyValueFactory<>("matches"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        drawsColumn.setCellValueFactory(new PropertyValueFactory<>("draws"));
        losesColumn.setCellValueFactory(new PropertyValueFactory<>("loses"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        allPlayers.setAll(LoginConnection.getInstance().getChessPlayerslist());
        statistics.getItems().setAll(allPlayers);
    }
    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }



}
