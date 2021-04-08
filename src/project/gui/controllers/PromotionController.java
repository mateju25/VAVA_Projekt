package project.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import project.model.gameChess.pieces.*;

public class PromotionController {
    public Pane pane;

    public Piece chosedPiece = null;
    private boolean isBlack;

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public Piece getChosedPiece() {
        return chosedPiece;
    }

    public void chooseQueen(ActionEvent actionEvent) {
        chosedPiece= new Queen(isBlack);
        close();
    }

    public void chooseBishop(ActionEvent actionEvent) {
        chosedPiece= new Bishop(isBlack);
        close();
    }

    public void chooseKnight(ActionEvent actionEvent) {
        chosedPiece= new Knight(isBlack);
        close();
    }

    public void chooseRook(ActionEvent actionEvent) {
        chosedPiece= new Rook(isBlack);
        close();
    }

    private void close() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
}
