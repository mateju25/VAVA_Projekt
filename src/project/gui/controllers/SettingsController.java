package project.gui.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.pieces.Piece;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private AnchorPane pane1;
    @FXML
    private AnchorPane pane2;
    @FXML
    private AnchorPane pane4;
    @FXML
    private AnchorPane pane5;
    private int positionBoard;
    private int positionFigure;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        translateAnimation(pane4,0.001,0);
        translateAnimation(pane5,0.001,0);
        int favF = LoginConnection.getInstance().getFavouritePieces();
        int favB = LoginConnection.getInstance().getFavouriteBoard();
        if(favB==1)
        {
            positionBoard=0;
            translateAnimation(pane1,0.001,0);
            translateAnimation(pane2,0.001,0);
        }
        if(favB==2)
        {
            positionBoard=1;
            translateAnimation(pane1,0.001,334);
            translateAnimation(pane2,0.001,0);
        }
        if(favB==3)
        {
            positionBoard=2;
            translateAnimation(pane1,0.001,334);
            translateAnimation(pane2,0.001,334);
        }
        if(favF==1)
        {
            positionFigure=0;
            translateAnimation(pane4,0.001,0);
            translateAnimation(pane5,0.001,0);
        }
        if(favF==2)
        {
            positionFigure=1;
            translateAnimation(pane4,0.001,130);
            translateAnimation(pane5,0.001,0);
        }
        if(favF==3)
        {
            positionFigure=2;
            translateAnimation(pane4,0.001,130);
            translateAnimation(pane5,0.001,130);
        }
    }

    private void translateAnimation(Node node,double duration, double width) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration),node);
        translateTransition.setByX(width);
        translateTransition.play();
    }
    @FXML
    private void nextImage(){
        if(positionBoard==0){
            translateAnimation(pane1,0.5,334);
            positionBoard++;
        }
        else if (positionBoard==1){
            translateAnimation(pane2,0.5,334);
            positionBoard++;
        }
    }
    @FXML
    private void previousImage(){
        if(positionBoard==1){
            translateAnimation(pane1,0.5,-334);
            positionBoard--;
        }
        else if (positionBoard==2){
            translateAnimation(pane2,0.5,-334);
            positionBoard--;
        }
    }
    @FXML
    private void nextImageFigure(){
        if(positionFigure==0){
            translateAnimation(pane4,0.5,+130);
            positionFigure++;
        }
        else if (positionFigure==1){
            translateAnimation(pane5,0.5,+130);
            positionFigure++;
        }
    }
    @FXML
    private void previousImageFigure(){
        if(positionFigure==1){
            translateAnimation(pane4,0.5,-130);
            positionFigure--;
        }
        else if (positionFigure==2){
            translateAnimation(pane5,0.5,-130);
            positionFigure--;
        }
    }
    @FXML
    private void saveSettings() {
        LoginConnection.getInstance().setFavouriteBoard(positionBoard+1);
        LoginConnection.getInstance().setFavouritePieces(positionFigure+1);
        Piece.SetNumber = positionFigure+1;
    }
    @FXML
    private void changeSceneMenu() throws IOException {

        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }


}