package project.gui.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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
    private int positionBoard = 0;
    private int positionFigure = 0;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        translateAnimation(pane1,0);
        translateAnimation(pane2,0);
        translateAnimation(pane4,0);
        translateAnimation(pane5,0);
    }

    private void translateAnimation(Node node, double width) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),node);
        translateTransition.setByX(width);
        translateTransition.play();
    }
    @FXML
    private void nextImage(){
        if(positionBoard==0){
            translateAnimation(pane1,334);
            positionBoard++;
        }
        else if (positionBoard==1){
            translateAnimation(pane2,334);
            positionBoard++;
        }
    }
    @FXML
    private void previousImage(){
        if(positionBoard==1){
            translateAnimation(pane1,-334);
            positionBoard--;
        }
        else if (positionBoard==2){
            translateAnimation(pane2,-334);
            positionBoard--;
        }
    }
    @FXML
    private void nextImageFigure(){
        if(positionFigure==0){
            translateAnimation(pane4,+130);
            positionFigure++;
        }
        else if (positionFigure==1){
            translateAnimation(pane5,+130);
            positionFigure++;
        }
    }
    @FXML
    private void previousImageFigure(){
        if(positionFigure==1){
            translateAnimation(pane4,-130);
            positionFigure--;
        }
        else if (positionFigure==2){
            translateAnimation(pane5,-130);
            positionFigure--;
        }
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }


}