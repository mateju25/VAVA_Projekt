package project.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import project.model.databaseSystem.ChessPlayer;
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
    private TextField round;
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
    private final ObservableList<Integer> counts = FXCollections.observableArrayList(4,8,12);



    private final Tournament tournament = new Tournament();
    //getnem si active playera
    private final ChessPlayer activePlayer = new ChessPlayer("Cheffe","kkt","sda",true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        activePlayer.setAdministrator(false);
        tournament.setActive(true);




        participantsCount.setItems(counts);
        if(activePlayer.isAdministrator()){
            if(!tournament.isActive()){
                round.setVisible(false);
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
            round.setVisible(true);
            match.setVisible(true);
            result.setVisible(true);
            warning.setVisible(false);
            buttonCreate.setVisible(false);
            buttonSave.setVisible(false);
            participantsCount.setVisible(false);
            description.setVisible(true);
            description.setText("Zapísať výsledok");
            return;

        }
        if(!tournament.isActive()){
            round.setVisible(false);
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
        round.setVisible(false);
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
        round.setVisible(true);
        match.setVisible(true);
        result.setVisible(true);
        description.setVisible(true);
        description.setText("Zapísať výsledok");
        //display brackets

    }
    private void displayBrackets(){
        if(participantsCount.getValue().equals(4)){

        }
        if(participantsCount.getValue().equals(8)){

        }
        if(participantsCount.getValue().equals(12)){

        }
    }
    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }


}
