package project.gui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.gui.Main;
import project.model.gameChess.Chessboard;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;
import project.model.gameChess.Signalization;
import project.model.gameChess.pieces.Pawn;
import project.model.gameChess.pieces.Piece;
import project.model.gameChess.pieces.Queen;
import project.model.stockfishApi.Stockfish;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class GameBoardController implements Initializable {


    public Canvas canvas;
    public TextArea textMoves;
    private int sizeOfSquare;
    private Chessboard board = null;


    private boolean stateLegalMoves = false;
    private Coordinates activeFigure = null;
    private ArrayList<Coordinates> legalMovesOfFigure;

    private Stockfish computer = Stockfish.getInstance();

    public void drawPiece(int x, int y, Piece piece) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(piece.getPic(),x*(sizeOfSquare),y*(sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void drawLegalMovePoint(int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(rgb(170, 170, 170));
        gc.fillOval(x*(sizeOfSquare)+35, y*(sizeOfSquare)+35, sizeOfSquare-70, sizeOfSquare-70);
    }

    public void drawSelectRenctangle(int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(rgb(34, 34, 34));
        gc.setLineWidth(5);
        gc.strokeRect(x * (sizeOfSquare), y * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void drawCheckRectangle() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Coordinates king = board.getState().whereIsThis(board.getState().isChecked(board.getState()));
        gc.setStroke(rgb(250, 0, 0));
        gc.setLineWidth(5);
        gc.strokeRect(king.getX() * (sizeOfSquare), king.getY() * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void refreshBoard() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (board.getState().isBlackCloser())
            gc.drawImage(new Image("/project/gui/resources/pictures/BlackBoard.png"),0,0,canvas.getWidth(),canvas.getHeight());
        else
            gc.drawImage(new Image("/project/gui/resources/pictures/WhiteBoard.png"),0,0,canvas.getWidth(),canvas.getHeight());
        GameState temp = board.getState();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (temp.getPieceOnPlace(i,j) != null)
                    drawPiece(i, j, temp.getPieceOnPlace(i,j));
            }
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        Main.primaryStage.setMaximized(true);

        sizeOfSquare = (int) ((canvas.getWidth()) / 8);
        board = new Chessboard(false);
        refreshBoard();
    }

    public Piece choosePromotion() {
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/project/gui/views/promotionDialog.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);

        PromotionController controller = loader.getController();
        controller.setBlack(!board.isBlackTurn());

        Stage window = new Stage();
        window.setResizable(false);
        window.setAlwaysOnTop(true);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setScene(scene);
        window.showAndWait();
        return controller.getChosedPiece();
    }

    public void onClick(MouseEvent mouseEvent) {
        int x = (int) ((mouseEvent.getX()) / sizeOfSquare);
        int y = (int) ((mouseEvent.getY()) / sizeOfSquare);

        if (stateLegalMoves) {
            stateLegalMoves = false;

            Signalization result = board.makeMove(activeFigure.getX(), activeFigure.getY(), x, y, null);
            if (result == Signalization.NOPIECE)
                return;

            if ((y == 0 || y == 7) && board.getState().getPieceOnPlace(x, y) instanceof Pawn) {
                Piece tmp = choosePromotion();
                board.getState().getState()[x][y] = tmp == null ? new Queen(!board.isBlackTurn()) : tmp;
            }

            refreshBoard();
            switch (result) {
                case CHECK: drawCheckRectangle();
                            break;
                case CHECKMATE:
            }
            textMoves.setText(textMoves.getText() + board.getAllMoves().getLast()  + ", " );
            String newMove = computer.getBestMove(board.getAllMoves().getLast());
            board.makeMove(newMove);
            textMoves.setText(textMoves.getText() + "\n" + newMove);
            refreshBoard();

        } else {
            refreshBoard();

            ArrayList<Coordinates> legalMoves = board.getLegalMoves(x, y);
            if (legalMoves == null)
                return;
            drawSelectRenctangle(x, y);
            legalMoves.forEach(coordinates -> drawLegalMovePoint(coordinates.getX(), coordinates.getY()));
            activeFigure = new Coordinates(x, y);
            stateLegalMoves = true;
        }
    }
}
