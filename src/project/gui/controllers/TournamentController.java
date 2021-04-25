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

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import project.model.databaseSystem.ChessPlayer;
import project.model.databaseSystem.LoginConnection;
import project.model.databaseSystem.Tournament;

import java.io.IOException;
import java.net.URL;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.rgb;

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
    private Button buttonJoin;
    @FXML
    private ComboBox<Integer> participantsCount;
    private final ObservableList<Integer> counts = FXCollections.observableArrayList(4,8);
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    //si ho len getnem ten turnaj toto treba spravit prve
    private final Tournament tournament= Tournament.getInstance();
    private final ChessPlayer activePlayer = LoginConnection.getInstance().getActivePlayer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tournament.setFormat(tournament.loadType());
        tournament.setMapOfParticipants(tournament.loadMapOfParticipantsFromDatabase());
        if (tournament.loadType()!=0) {
            tournament.setActive(true);
        }
        gc=canvas.getGraphicsContext2D();
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
        for (String key:tournament.getMapOfParticipants().keySet()
        ) {
            if (Integer.parseInt(key) < 12)
                joinTournamentBrackets(Integer.parseInt(key),tournament.getMapOfParticipants().get(key));
            else
                addResult(key,tournament.getMapOfParticipants().get(key));
        }
    }

    private void setActiveTournamentAdmin(){
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
        buttonJoin.setVisible(!activePlayer.isParticipant());
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
        match.setVisible(false);
        result.setVisible(false);
        warning.setVisible(true);
        buttonCreate.setVisible(false);
        buttonSave.setVisible(false);
        buttonJoin.setVisible(false);
        participantsCount.setVisible(false);
        description.setVisible(false);
    }
    private void joinTournamentBrackets(int x, String name){
        if(tournament.getFormat() == 8) {
            int temp = x;
            int gen = 0;
            while (temp != 1 && temp != 0) {
                temp = temp - 2;
                gen++;
            }
            if (x % 2 == 1) {
                gc.fillText(name, 65, 40 + (94 * (x - gen - 1)));

            } else {

                gc.fillText(name, 65, 88 + (94 * (x - gen - 1)));
            }
        }
        if(tournament.getFormat() == 4){
            if (x == 1) {
                gc.fillText(name, 68, 40);
            } else {
                gc.fillText(name, 68, 40+(100 * (x-1)));
            }
        }
    }
    private void displayBrackets(){
        Image brackets=null;
        if(tournament.getFormat()==4){
            brackets = new Image("/project/gui/resources/pictures/turnaj4.png");
        }
        if(tournament.getFormat()==8){
            brackets = new Image("/project/gui/resources/pictures/turnaj8.png");
        }
        gc.drawImage(brackets, 0, 0);

    }
    @FXML
    private void createTournament(){
        if(participantsCount.getSelectionModel().getSelectedItem()==null)
        {
                return;
        }
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
        int x=1;
        joinTournamentBrackets(x,activePlayer.getName());
        buttonJoin.setVisible(false);
        String pos = getAvailablePosition();
        if(pos!=null){
            tournament.getMapOfParticipants().put(pos,activePlayer.getName());
            tournament.setMapOfParticipantsToDatabase();
        }
    }
    @FXML
    private void addResult() {

        String matchround = match.getText();
        String winner = result.getText();
        if (tournament.getMapOfParticipants().containsKey(matchround)) {
            return;
        }
        addResult(matchround,winner);
    }
    private void addResult(String matchround,String winner){

        if (tournament.getFormat() == 8) {
            if (matchround.equals("12")) {
                gc.fillText(winner, 270, 64);
                tournament.getMapOfParticipants().put("12",winner);
            }
            if (matchround.equals("34")) {
                gc.fillText(winner, 270, 158);
                tournament.getMapOfParticipants().put("34",winner);
                }
            }
            if (matchround.equals("56")) {
                gc.fillText(winner, 270, 252);
                tournament.getMapOfParticipants().put("56",winner);
            }
            if (matchround.equals("78")) {

                gc.fillText(winner, 270, 346);
                tournament.getMapOfParticipants().put("78",winner);
            }
            if (matchround.equals("1234")) {
                gc.fillText(winner, 440, 109);
                tournament.getMapOfParticipants().put("1234",winner);
            }
            if (matchround.equals("5678")) {
                gc.fillText(winner, 440, 302);
                tournament.getMapOfParticipants().put("5678",winner);
            }
            if (matchround.equals("12345678")) {
                gc.fillText(winner, 610, 205);
                tournament.getMapOfParticipants().put("12345678",winner);
        }
        if(tournament.getFormat()==4)
        {
            if (matchround.equals("12")) {
                gc.fillText(winner, 310, 95);
                tournament.getMapOfParticipants().put("12",winner);
            }
            if (matchround.equals("34")) {
                gc.fillText(winner, 310, 320);
                tournament.getMapOfParticipants().put("34",winner);
            }
            if (matchround.equals("1234")) {
                gc.fillText(winner, 550, 207);
                tournament.getMapOfParticipants().put("1234",winner);
            }
        }
        tournament.setMapOfParticipantsToDatabase();
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }


}
