package project.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import project.model.gameChess.pieces.*;

public class PromotionController {
    public Pane pane;

    public String chosedPiece = "q";

    public String getChosedPiece() {
        return chosedPiece;
    }

    public void chooseQueen(ActionEvent actionEvent) {
        chosedPiece= "q";
        close();
    }

    public void chooseBishop(ActionEvent actionEvent) {
        chosedPiece= "b";
        close();
    }

    public void chooseKnight(ActionEvent actionEvent) {
        chosedPiece= "k";
        close();
    }

    public void chooseRook(ActionEvent actionEvent) {
        chosedPiece= "r";
        close();
    }

    private void close() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
}
