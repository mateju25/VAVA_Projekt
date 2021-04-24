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

import java.util.ResourceBundle;

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
    private final Tournament tournament = new Tournament();
    private final ChessPlayer activePlayer = LoginConnection.getInstance().getActivePlayer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc=canvas.getGraphicsContext2D();
        gc.setFill(rgb(255,255,255));
        gc.setFont(new Font("serif",  20));
        participantsCount.setItems(counts);
        tournament.setActive(false);//toto pojde dpc

        if(activePlayer.isAdministrator()){
            if(!tournament.isActive()){
                setNoActiveTournamentAdmin();
                return;
            }
            setActiveTournamentAdmin();
            displayBrackets();
            //tuto budu tie loopy na vykreslovanie
            return;
        }
        if(!tournament.isActive()){
            setNoActiveTournamentPlayer();
            return;
        }
        setActiveTournamentPlayer();
        displayBrackets();
        //tuto budu tie loopy na vyhreslovanie

    }
    private void setActiveTournamentAdmin(){
        //if player joined tournament buttonJoin.setVisible(false);
        //else buttonJoin.setVisible(true);
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
        //if player joined tournament buttonJoin.setVisible(false);
        //else buttonJoin.setVisible(true);
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
    private void setNameIntoBrackets(int x,int format,ChessPlayer player){
        //x je cislo najblizsej volnej pozicie teda kam sa ide zapisat meno inak toto je dobre
        //zavolam z initialize tuto musim podla udajov z hashmapy vypisat mena na ich aktualne pozicie
        //namiesto active player bude aktualny v loope
        if(format==8) {
            int temp = x;
            int gen = 0;
            while (temp != 1 && temp != 0) {
                temp = temp - 2;
                gen++;
            }
            if (x % 2 == 1) {
                gc.fillText(player.getName(), 65, 40 + (94 * (x - gen - 1)));

            } else {

                gc.fillText(player.getName(), 65, 88 + (94 * (x - gen - 1)));
            }
        }
        if(format==4){
            if (x == 1) {
                gc.fillText(player.getName(), 68, 40);
            } else {
                gc.fillText(player.getName(), 68, 40+(100 * (x-1)));
            }
        }
    }
    private void displayBrackets(){
        //jednorazovo vykresli brackets
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
        //toto je hotove
        if(participantsCount.getValue()!=8&&(participantsCount.getValue()!=4))
        {
            //error
                return;
        }
        tournament.setActive(true);
        tournament.setFormat(participantsCount.getValue());

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


    @FXML
    private void joinTournament(){
        //toto xko bude to cislo ku ktoremu idem pisat meno v brackets
        gc.setFill(rgb(0,255,255));
        int x=1;
        setNameIntoBrackets(x, tournament.getFormat(),activePlayer);
        buttonJoin.setVisible(false);
        //setMapOfParticipants
        //setMapOfParticipantsToDatabase
    }
    @FXML
    private void addResult() {
        //toto Som len testoval kurva :D
        //gc.fillText(activePlayer.getName(),245,346);
        int matchround = Integer.parseInt(match.getText());
        int winner = Integer.parseInt(result.getText());
        //z mapy vytiahnem zapas ktoreho hladam winnera
        //osetrim ci zapas existuje a ci este nema winnera
        //270+270+270 X a 64+94+94+94 ---- 109+193-97
        if (tournament.getFormat() == 8) {
            if (matchround == 12) {
                if (winner == 1) {
                    gc.fillText(activePlayer.getName(), 270, 64);
                } else if (winner == 2) {
                    gc.fillText(activePlayer.getName(), 270, 64);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 34) {
                if (winner == 3) {
                    gc.fillText(activePlayer.getName(), 270, 158);
                } else if (winner == 4) {
                    gc.fillText(activePlayer.getName(), 270, 158);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 56) {
                if (winner == 5) {
                    gc.fillText(activePlayer.getName(), 270, 252);
                } else if (winner == 6) {
                    gc.fillText(activePlayer.getName(), 270, 252);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 78) {
                if (winner == 7) {
                    gc.fillText(activePlayer.getName(), 270, 346);
                } else if (winner == 8) {
                    gc.fillText(activePlayer.getName(), 270, 346);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 1234) {
                if (winner == 12) {
                    gc.fillText(activePlayer.getName(), 440, 109);
                } else if (winner == 34) {
                    gc.fillText(activePlayer.getName(), 440, 109);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 5678) {
                if (winner == 56) {
                    gc.fillText(activePlayer.getName(), 440, 302);
                } else if (winner == 78) {
                    gc.fillText(activePlayer.getName(), 440, 302);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 12345678) {
                if (winner == 1234) {
                    gc.fillText(activePlayer.getName(), 610, 205);
                } else if (winner == 5678) {
                    gc.fillText(activePlayer.getName(), 610, 205);
                } else {
                    //error
                    return;
                }
            }
        }
        if(tournament.getFormat()==4)
        {
            if (matchround == 12) {
                if (winner == 1) {
                    gc.fillText(activePlayer.getName(), 310, 95);
                } else if (winner == 2) {
                    gc.fillText(activePlayer.getName(), 310, 95);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 34) {
                if (winner == 3) {
                    gc.fillText(activePlayer.getName(), 310, 320);
                } else if (winner == 4) {
                    gc.fillText(activePlayer.getName(), 310, 320);
                } else {
                    //error
                    return;
                }
            }
            if (matchround == 1234) {
                if (winner == 12) {
                    gc.fillText(activePlayer.getName(), 550, 207);
                } else if (winner == 34) {
                    gc.fillText(activePlayer.getName(), 550, 207);
                }
            }
        }
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }


}
