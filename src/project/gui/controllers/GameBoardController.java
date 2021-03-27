package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import project.gui.Main;
import project.model.gameChess.Chessboard;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;
import project.model.gameChess.pieces.Piece;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class GameBoardController implements Initializable {


    public Canvas canvas;
    private int sizeOfSquare;
    private Chessboard board = null;


    private boolean stateLegalMoves = false;
    private int activeFigureX;
    private int activeFigureY;
    private ArrayList<Coordinates> legalMovesOfFigure;

    public void drawPiece(int x, int y, Piece piece) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(piece.getPic(),x*(sizeOfSquare),y*(sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void drawLegalMovePoint(int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(rgb(128, 255, 255));
        gc.fillOval(x*(sizeOfSquare)+45, y*(sizeOfSquare)+45, sizeOfSquare-90, sizeOfSquare-90);
    }

    public void refreshBoard() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(new Image("/project/gui/resources/pictures/newBoard.png"),0,0,canvas.getWidth(),canvas.getHeight());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getState().getPieceOnPlace(i,j) != null)
                    drawPiece(i, j, board.getState().getPieceOnPlace(i,j));
            }
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        Main.primaryStage.setMaximized(true);

        sizeOfSquare = (int) ((canvas.getWidth()) / 8);
        board = new Chessboard(new GameState());
        board.getState().setNewStateStandardWhiteFiguresCloser();
        refreshBoard();


















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

    public void onClick(MouseEvent mouseEvent) {
        int x = (int) ((mouseEvent.getX()) / sizeOfSquare);
        int y = (int) ((mouseEvent.getY()) / sizeOfSquare);

        if (stateLegalMoves) {
            stateLegalMoves = false;
            boolean isThere = false;
            if (legalMovesOfFigure == null) {
                refreshBoard();
                return;
            }
            for (Coordinates coors :
                    legalMovesOfFigure) {
                if (coors.getX() == x && coors.getY() == y)
                    isThere = true;
            }


            if (isThere)
                board.makeMove(activeFigureX, activeFigureY, x, y);
            refreshBoard();
        } else {
            refreshBoard();
            if (board.getState().getPieceOnPlace(x, y) == null)
                return;
            ArrayList<Coordinates> legalMoves = board.getLegalMoves(x, y);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(rgb(128, 255, 255));
            gc.setLineWidth(5);
            gc.strokeRect(x * (sizeOfSquare), y * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
            if (legalMoves != null) {
                for (Coordinates coors :
                        legalMoves) {
                    drawLegalMovePoint(coors.getX(), coors.getY());
                }
                legalMovesOfFigure = legalMoves;
                activeFigureX = x;
                activeFigureY = y;

            }
            stateLegalMoves = true;
        }
    }
}
