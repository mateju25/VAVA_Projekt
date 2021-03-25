package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import project.gui.Main;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {
    @FXML
    private Label a1,b1,c1,d1,e1,f1,g1,h1;
    @FXML
    private Label a2,b2,c2,d2,e2,f2,g2,h2;
    @FXML
    private Label a7,b7,c7,d7,e7,f7,g7,h7;
    @FXML
    private Label a8,b8,c8,d8,e8,f8,g8,h8;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.primaryStage.setMaximized(true);
        // set all to default
        ImageView imageView = new ImageView();
        //////////////////////////////////////////////////////
        imageView.setOnMouseClicked(e -> {
            System.out.println("["+e.getX()+", "+e.getY()+"]");
            //get legal moves
    });
       // show legal moves --> vykresli na sachovici bodky

        //if on mouse clicked got position from legal moves prekresli sachovnicu


    }


}
