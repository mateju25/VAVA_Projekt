package project.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import project.model.databaseSystem.ChessPlayer;
import project.model.databaseSystem.LoginConnection;
import project.model.databaseSystem.Tournament;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.rgb;

public class TournamentController {
    @FXML
    public Button buttonCancelTournament;
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
    private Button buttonJoin;
    @FXML
    private ComboBox<Integer> participantsCount;
    private final ObservableList<Integer> counts = FXCollections.observableArrayList(4,8);
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;

    private final Tournament tournament= Tournament.getInstance();
    private final ChessPlayer activePlayer = LoginConnection.getInstance().getActivePlayer();

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        tournament.setFormat(tournament.loadType());
        tournament.setMapOfParticipants(tournament.loadMapOfParticipantsFromDatabase());
        if (tournament.getFormat() != 0) {
            tournament.setActive(true);
        }

        gc.setFill(rgb(255,255,255));
        gc.setFont(new Font("serif",  20));
        participantsCount.setItems(counts);

//        tournament.setMapOfParticipants(new HashMap<>());
//        tournament.setMapOfParticipantsToDatabase();

        if(activePlayer.isAdministrator()){
            if(!tournament.isActive()){
                setNoActiveTournamentAdmin();
                return;
            }
            setActiveTournamentAdmin();
            setNamesIntoBrackets();
            return;
        }
        if(!tournament.isActive()){
            setNoActiveTournamentPlayer();
            return;
        }
        setActiveTournamentPlayer();
        setNamesIntoBrackets();

    }

    private void setNamesIntoBrackets() {
        displayBrackets();
        for (String key:tournament.getMapOfParticipants().keySet())
            addNameIntoBracket(key, tournament.getMapOfParticipants().get(key));
    }

    private void setActiveTournamentAdmin() {
        buttonCancelTournament.setVisible(true);
        buttonJoin.setVisible(!activePlayer.isParticipant());
        match.setVisible(true);
        result.setVisible(true);
        warning.setVisible(false);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(true);
        participantsCount.setVisible(false);
        description.setVisible(true);
        description.setText("Zapísať výsledok");
    }

    private void setNoActiveTournamentAdmin(){
        buttonCancelTournament.setVisible(false);
        match.setVisible(false);
        result.setVisible(false);
        warning.setVisible(true);
        buttonCreate.setVisible(true);
        buttonSave.setVisible(false);
        buttonJoin.setVisible(false);
        participantsCount.setVisible(true);
        description.setVisible(true);
        description.setText("Počet účastníkov");
    }

    private void setActiveTournamentPlayer(){
        buttonCancelTournament.setVisible(false);
        buttonJoin.setVisible(!activePlayer.isParticipant());
        if (tournament.getMapOfParticipants().containsKey(String.valueOf(tournament.getFormat())))
            buttonJoin.setVisible(false);
        match.setVisible(false);
        result.setVisible(false);
        warning.setVisible(false);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(false);
        participantsCount.setVisible(false);
        description.setVisible(true);
        description.setText("Nech zvíťazi ten najlepší, veľa štastia!");
    }

    private void setNoActiveTournamentPlayer(){
        buttonCancelTournament.setVisible(false);
        match.setVisible(false);
        result.setVisible(false);
        warning.setVisible(true);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(false);
        buttonJoin.setVisible(false);
        participantsCount.setVisible(false);
        description.setVisible(false);
    }

    private void displayBrackets(){
        if (tournament.getFormat() != 0) {
            String url = "/project/gui/resources/pictures/turnaj" + tournament.getFormat() + ".png";
            gc.drawImage(new Image(url), 0, 0);
        }
    }

    @FXML
    private void createTournament(){
        if(participantsCount.getSelectionModel().getSelectedItem()==null)
            return;

        tournament.setActive(true);
        tournament.setFormat(participantsCount.getValue());
        tournament.setType();

        match.setVisible(true);
        result.setVisible(true);
        warning.setVisible(false);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(true);
        buttonJoin.setVisible(true);
        participantsCount.setVisible(false);
        description.setVisible(true);
        description.setText("Zapísať výsledok");
        displayBrackets();
        initialize();
    }

    private String getAvailablePosition() {
        int pos=0;
        for (String key:tournament.getMapOfParticipants().keySet().stream().sorted((a,b)->b.length()-a.length()).collect(Collectors.toList())
        ) {
            if (Integer.parseInt(key) < 12) {
                pos=Integer.parseInt(key);
            }
        }
        if(pos<tournament.getFormat()){
            pos++;
        }
        else {
            pos=-1;
        }

        return pos==-1 ? null : String.valueOf(pos);
    }

    @FXML
    private void joinTournament(){
        gc.setFill(rgb(0,255,255));
        buttonJoin.setVisible(false);
        String pos = getAvailablePosition();
        if(pos!=null){
            tournament.getMapOfParticipants().put(pos,activePlayer.getName());
            tournament.setMapOfParticipantsToDatabase();
            addNameIntoBracket(pos, activePlayer.getName());
            activePlayer.setParticipant(true);
            LoginConnection.getInstance().saveUser();
        }
    }

    @FXML
    private void addResult() {
        if (tournament.getMapOfParticipants().containsKey(match.getText()))
            return;

        addNameIntoBracket(match.getText(),result.getText());
    }

    private void addNameIntoBracket(String x, String name) {
        if (Integer.parseInt(x) < 12)
            addParticipant(x, name);
        else
            addResult(x, name);
    }

    private void addParticipant(String x, String name){
        if(tournament.getFormat() == 8) {
            int temp = Integer.parseInt(x);
            int gen = 0;
            while (temp != 1 && temp != 0) {
                temp = temp - 2;
                gen++;
            }
            if (Integer.parseInt(x) % 2 == 1) {
                gc.fillText(name, 65, 40 + (94 * (Integer.parseInt(x) - gen - 1)));

            } else {

                gc.fillText(name, 65, 88 + (94 * (Integer.parseInt(x) - gen - 1)));
            }
        }
        if(tournament.getFormat() == 4){
            if (Integer.parseInt(x) == 1) {
                gc.fillText(name, 68, 40);
            } else {
                gc.fillText(name, 68, 40+(100 * (Integer.parseInt(x)-1)));
            }
        }
    }

    private void addResult(String matchround,String winner){
        if (tournament.getFormat() == 0) {
            return;
        }
        if (tournament.getFormat() == 8) {
            switch (matchround.intern()) {
                case "12": gc.fillText(winner, 270, 64); break;
                case "34": gc.fillText(winner, 270, 158); break;
                case "56": gc.fillText(winner, 270, 252); break;
                case "78": gc.fillText(winner, 270, 346); break;
                case "1234": gc.fillText(winner, 440, 109); break;
                case "5678": gc.fillText(winner, 440, 302); break;
                case "12345678": gc.fillText(winner, 610, 205); break;
            }
        }
        if (tournament.getFormat() == 4) {
            switch (matchround.intern()) {
                case "12": gc.fillText(winner, 310, 95); break;
                case "34": gc.fillText(winner, 310, 320); break;
                case "1234": gc.fillText(winner, 550, 207); break;
            }
        }
        tournament.getMapOfParticipants().put(matchround,winner);
        tournament.setMapOfParticipantsToDatabase();
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }

    @FXML
    public void cancelTournament(ActionEvent actionEvent) {
        tournament.deleteTournament();
        initialize();
    }
}
