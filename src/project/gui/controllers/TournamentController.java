package project.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import project.model.databaseSystem.ChessPlayer;
import project.model.databaseSystem.LoginConnection;
import project.model.databaseSystem.Tournament;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TournamentController implements Initializable {
    @FXML
    private Label warning;
    @FXML
    private Text description;
    @FXML
    private TextField match;
    @FXML
    private TextField result;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonSave;
    @FXML
    private ComboBox<Integer> participantsCount;
    private final ObservableList<Integer> counts = FXCollections.observableArrayList(4,8);
    @FXML
    private Canvas canvas;


    //si ho len getnem ten turnaj
    private final Tournament tournament = new Tournament();
    private final ChessPlayer activePlayer = LoginConnection.getInstance().getActivePlayer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        tournament.setActive(false);




        participantsCount.setItems(counts);
        if(activePlayer.isAdministrator()){
            if(!tournament.isActive()){

                match.setVisible(false);
                result.setVisible(false);
                warning.setVisible(true);
                buttonCreate.setVisible(true);
                buttonSave.setVisible(false);
                description.setVisible(true);
                description.setText("Počet účastníkov");
                participantsCount.setVisible(true);

                return;
            }
            //displaybrackets

            match.setVisible(true);
            result.setVisible(true);
            warning.setVisible(false);
            buttonCreate.setVisible(false);
            buttonSave.setVisible(true);
            participantsCount.setVisible(false);
            description.setVisible(true);
            description.setText("Zapísať výsledok");
            return;

        }
        if(!tournament.isActive()){

            match.setVisible(false);
            result.setVisible(false);
            warning.setVisible(true);
            buttonCreate.setVisible(false);
            buttonSave.setVisible(false);
            participantsCount.setVisible(false);
            description.setVisible(false);
            return;
        }
        //displayBrackets();

        match.setVisible(false);
        result.setVisible(false);
        warning.setVisible(false);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(false);
        participantsCount.setVisible(false);
        description.setVisible(false);
    }

    @FXML
    private void createTournament(){
        tournament.setActive(true);
        warning.setVisible(false);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(true);
        description.setVisible(false);
        participantsCount.setVisible(false);
        tournament.setActive(true);

        match.setVisible(true);
        result.setVisible(true);
        description.setVisible(true);
        description.setText("Zapísať výsledok");
        displayBrackets();

    }
    private void displayBrackets(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image brackets=null;
        if(participantsCount.getValue().equals(4)){

            brackets = new Image("/project/gui/resources/pictures/turnaj4.png");

            }
        if(participantsCount.getValue().equals(8)){

            brackets = new Image("/project/gui/resources/pictures/turnaj8.png");

        }
        gc.drawImage(brackets, 0, 0);
    }
    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }


}
