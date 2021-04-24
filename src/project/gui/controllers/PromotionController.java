package project.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.pieces.*;

public class PromotionController {
    public Pane pane;

    public String chosedPiece = "q";
    public ImageView queen;
    public ImageView bishop;
    public ImageView rook;
    public ImageView knight;

    public void initialize() {
        int piece = LoginConnection.getInstance().getFavouritePieces();
        String color = GameBoardController.board.getState().isBlackCloser() ? "Black" : "White";
        queen.setImage(new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + piece + "/" + color + "Queen.png")));
        bishop.setImage(new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + piece + "/" + color + "Bishop.png")));
        rook.setImage(new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + piece + "/" + color + "Rook.png")));
        knight.setImage(new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + piece + "/" + color + "Knight.png")));
    }

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
        chosedPiece= "n";
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
