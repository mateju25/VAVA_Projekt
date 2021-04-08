package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.SingleSelectionModel;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class GameBoardController implements Initializable {

    @FXML public Canvas canvas;
    @FXML public TextArea textMoves;

    private GraphicsContext gc = null;
    private int sizeOfSquare;
    private Chessboard board = null;

    private Coordinates activeFigure = null;
    private Stockfish computer = Stockfish.getInstance(1);
    private Signalization lastSignal = Signalization.NORMAL;

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

    public void drawCheckRectangle() {
        Coordinates king = board.getState().whereIsThis(board.getState().isChecked(board.getState(), board.isBlackTurn()));
        gc.setStroke(rgb(250, 0, 0));
        gc.setLineWidth(5);
        gc.strokeRect(king.getX() * (sizeOfSquare), king.getY() * (sizeOfSquare), sizeOfSquare, sizeOfSquare);
    }

    public void refreshBoard() {
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
        if (lastSignal == Signalization.CHECK)
            drawCheckRectangle();
    }
    
    public void makeMoves(String moves) {
        String[] arr = moves.split(" ");
        for (String str :
                arr) {
            board.makeMove(str);
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        Main.primaryStage.setMaximized(true);

        sizeOfSquare = (int) ((canvas.getWidth()) / 8);
        board = new Chessboard(false);
        gc = canvas.getGraphicsContext2D();
        //makeMoves("f2f4 g8f6 g2g3 d7d5 d2d4 g7g6 e2e3 c8f5 f1d3 e7e6 d3f5 e6f5 b1c3 h7h5 h2h4 c7c6 g1f3 b8d7 f3e5 d7e5 f4e5 f6g4 e1g1 f7f6 e5f6 d8f6 b2b3 f8d6 a2a4 d6g3 c1b2 g3h4 e3e4 e8g8 e4f5 g6f5 c3e2 f8f7 e2f4 f7g7 f4h5 g4f2 ");
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

        if (activeFigure != null) {
            Signalization signal = lastSignal;
            lastSignal = board.makeMove(activeFigure.getX(), activeFigure.getY(), x, y, null);
            activeFigure = null;
            if (lastSignal == Signalization.NOPIECE) {
                lastSignal = signal;
                refreshBoard();
                return;
            }

            if ((y == 0 || y == 7) && board.getState().getPieceOnPlace(x, y) instanceof Pawn) {
                Piece tmp = choosePromotion();
                board.getState().getState()[x][y] = tmp == null ? new Queen(!board.isBlackTurn()) : tmp;
            }

            textMoves.setText(textMoves.getText() + board.getAllMoves().getLast()  + " " );
            String newMove = computer.getBestMove(board.getAllMoves().getLast());
            lastSignal = board.makeMove(newMove);
            textMoves.setText(textMoves.getText() + newMove + " " );
            refreshBoard();

            switch (lastSignal) {
                case CHECK: drawCheckRectangle();
                    break;
                case CHECKMATE:
                    canvas.setDisable(true);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("MAT");
                    alert.showAndWait();
            }
        } else {
            refreshBoard();
            ArrayList<Coordinates> legalMoves = board.getLegalMoves(x, y);
            if (legalMoves == null)
                return;
            drawSelectRenctangle(x, y);
            legalMoves.forEach(coordinates -> drawLegalMovePoint(coordinates.getX(), coordinates.getY()));
            activeFigure = new Coordinates(x, y);
        }
    }
}
