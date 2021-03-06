package project.gui.controllers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Chessboard;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;
import project.model.gameChess.Signalization;
import project.model.gameChess.pieces.Piece;

import static javafx.scene.paint.Color.rgb;

public class DrawingFunctions {
    private Canvas canvas = null;
    private GraphicsContext gc = null;
    private int sizeOfSquare;
    private Chessboard board = null;
    private Image backgroundBlack;
    private Image backgroundWhite;

    public DrawingFunctions(Canvas canvas, Chessboard board) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.sizeOfSquare = (int) ((canvas.getWidth()) / 8);
        this.board = board;
        this.backgroundBlack = new Image("/project/gui/resources/pictures/boards/set" + LoginConnection.getInstance().getActivePlayer().getFavouriteBoard() + "/BlackBoard.png");
        this.backgroundWhite = new Image("/project/gui/resources/pictures/boards/set" + LoginConnection.getInstance().getActivePlayer().getFavouriteBoard() + "/WhiteBoard.png");
    }

    public void drawPiece(int x, int y, Piece piece) {
        gc.drawImage(piece.getPic(),x*(sizeOfSquare),y*(sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void drawLegalMovePoint(int x, int y) {
        gc.setFill(rgb(170, 170, 170));
        gc.fillOval(x*(sizeOfSquare)+35, y*(sizeOfSquare)+35, sizeOfSquare-70, sizeOfSquare-70);
    }

    public void drawSelectRenctangle(int x, int y) {
        gc.setStroke(rgb(34, 34, 34));
        gc.setLineWidth(5);
        gc.strokeRect(x * (sizeOfSquare), y * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void drawCheckRectangle(Coordinates king) {
        gc.setStroke(rgb(250, 0, 0));
        gc.setLineWidth(5);
        gc.strokeRect(king.getX() * (sizeOfSquare), king.getY() * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void drawTransparentRecntangle(int x, int y) {
        gc.setFill(rgb(245, 95, 10, 0.75));
        gc.fillRect(x * (sizeOfSquare), y * (sizeOfSquare)-1, sizeOfSquare+1, sizeOfSquare+1);
    }
    public void drawMateRecntangle(Coordinates king) {
        gc.setFill(rgb(255, 0, 0, 0.8));
        gc.fillRect(king.getX() * (sizeOfSquare), king.getY() * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }


    public void refreshBoard() {
        if (board.getState().isBlackCloser())
            gc.drawImage(backgroundBlack,0,0,canvas.getWidth(),canvas.getHeight());
        else
            gc.drawImage(backgroundWhite,0,0,canvas.getWidth(),canvas.getHeight());
        GameState temp = board.getState();

        if (board.getAllMoves().size() != 0) {
            int startX , startY , finishX , finishY;
            if (board.getState().isBlackCloser()) {
                startX = 7-(board.getAllMoves().getLast().charAt(0) - 97);
                startY = Integer.parseInt(String.valueOf(board.getAllMoves().getLast().charAt(1)))-1;
                finishX = 7-(board.getAllMoves().getLast().charAt(2) - 97);
                finishY = Integer.parseInt(String.valueOf(board.getAllMoves().getLast().charAt(3)))-1;
            } else {
                startX = board.getAllMoves().getLast().charAt(0) - 97;
                startY = 7 - Integer.parseInt(String.valueOf(board.getAllMoves().getLast().charAt(1))) + 1;
                finishX = board.getAllMoves().getLast().charAt(2) - 97;
                finishY = 7 - Integer.parseInt(String.valueOf(board.getAllMoves().getLast().charAt(3))) + 1;
            }


            drawTransparentRecntangle(startX, startY);
            drawTransparentRecntangle(finishX, finishY);
        }

        if (board.getLastSignal() == Signalization.CHECK)
            drawCheckRectangle(board.getState().whereIsThis(board.getState().isChecked(board.getState(), board.isBlackTurn())));
        if (board.getLastSignal() == Signalization.CHECKMATE)
            drawMateRecntangle(board.getState().whereIsThis(board.getState().isChecked(board.getState(), board.isBlackTurn())));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (temp.getPieceOnPlace(i,j) != null)
                    drawPiece(i, j, temp.getPieceOnPlace(i,j));
            }
        }

    }
}
