package project.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class GameBoardController implements Initializable {

    @FXML public Canvas canvas;
    @FXML public TextArea textMoves;
    @FXML public Label topTimerText;
    @FXML public Label botTimerText;

    private LocalTime topTime;
    private LocalTime botTime;
    private Timer timer = null;
    private boolean stop = false;

    private DrawingFunctions drawingFunctions = null;
    private Chessboard board = null;
    private ArrayList<Coordinates> legalMoves;

    private Coordinates activeFigure = null;
    private Stockfish computer = Stockfish.getInstance(10);


    @Deprecated
    public void makeMoves(String moves) {
        String[] arr = moves.split(" ");
        for (String str :
                arr) {
            board.makeMove(str);
        }
    }

    public void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if(!stop && topTime.isAfter(LocalTime.of(0, 0, 0)) && botTime.isAfter(LocalTime.of(0, 0, 0)))
                {
                    if (board.isBlackTurn()) {
                        topTime = topTime.minusSeconds(1);
                    } else {
                        botTime = botTime.minusSeconds(1);
                    }
                    Platform.runLater(() -> {
                        topTimerText.setText(topTime.format(DateTimeFormatter.ofPattern("mm:ss")));
                        botTimerText.setText(botTime.format(DateTimeFormatter.ofPattern("mm:ss")));
                    });
                }
                else
                    timer.cancel();
            }
        }, 1000,1000);
    }

    public void initialize(URL location, ResourceBundle resources) {
        Main.primaryStage.setMaximized(true);

        board = new Chessboard(false);
        drawingFunctions = new DrawingFunctions(canvas, board);

        botTime = LocalTime.of(0, 5, 0);
        topTime = LocalTime.of(0, 5, 0);
        topTimerText.setText(topTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        botTimerText.setText(botTime.format(DateTimeFormatter.ofPattern("mm:ss")));

        Thread stockfishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String lastMove = board.getAllMoves().size() == 0 ? "" : board.getAllMoves().getLast();
                while (board.getLastSignal() != Signalization.CHECKMATE && board.getLastSignal() != Signalization.STALEMATE) {
                    while (lastMove.equals( board.getAllMoves().size() == 0 ? "" : board.getAllMoves().getLast())) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (this) {
                        String newMove = computer.getBestMove(board.getAllMoves().getLast());
                        if (!newMove.contains("none"))
                            board.makeMove(newMove);
                        lastMove = board.getAllMoves().getLast();
                        textMoves.setText(textMoves.getText() + newMove + " ");
                        drawingFunctions.refreshBoard();
                    }
                    Platform.runLater(() -> {
                        switch (board.getLastSignal()) {
                            case CHECK:
                                drawingFunctions.drawCheckRectangle(board.getState().whereIsThis(board.getState().isChecked(board.getState(), board.isBlackTurn())));
                                break;
                            case CHECKMATE:
                                stop = true;
                                canvas.setDisable(true);
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information Dialog");
                                alert.setHeaderText(null);
                                alert.setContentText("MAT");
                                alert.showAndWait();
                                break;
                            case STALEMATE:
                                stop = true;
                                canvas.setDisable(true);
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information Dialog");
                                alert.setHeaderText(null);
                                alert.setContentText("PAT");
                                alert.showAndWait();
                        }
                    });
                }
                stop = true;
            }
        });

        makeMoves("e2e4 e7e5 g1f3 d7d5 d1e2 d5e4 e2e4 b8c6 f3e5 d8e7 d2d3 c6e5 f1e2 e5g6 e4e7 f8e7 e1g1 c8d7 f1e1 e8d8 b1c3 c7c6 a2a3 e7f6 c3e4 f6e7 b2b3 f7f5 e4g5 e7g5 c1g5 g8f6 e2h5 b7b6 h5g6 h7g6 c2c4 a8b8 b3b4 h8e8 e1e8 d8e8 a1e1 e8f7 h2h3 b8b7 f2f4 b6b5 c4c5 a7a6 d3d4 d7c8 g5f6 g7f6 g2g4 b7d7 e1d1 d7d8 g4g5 d8g8 h3h4 f6g5 h4g5 g8h8 g1f2 h8h3 d1a1 c8e6 f2g2 e6c4 g2h3 c4e2 h3g3 f7f8 g3f2 e2c4 a3a4 b5a4 a1a4 c4b5 a4a5 f8e7 f2e3 e7d7 a5a1 d7d8 a1h1 b5c4 h1h7 a6a5 b4a5 d8c8 e3d2 c4b5 d2c3 c8b8 c3b4 b5d3 a5a6 d3a6 b4a5 a6e2 a5b6 e2a6 b6a6 b8c8 a6b6 c8d8 b6c6 d8e8 c6d6 e8f8 d6e6 f8g8 h7a7 g8f8 d4d5 f8g8 e6f6 g8h8 ");
        computer.setMoves(new LinkedList<>(board.getAllMoves()));
        stockfishThread.start();
        drawingFunctions.refreshBoard();
    }

    private String choosePromotion() {
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

        Stage window = new Stage();
        window.setResizable(false);
        window.setAlwaysOnTop(true);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setScene(scene);
        window.showAndWait();
        return controller.getChosedPiece();
    }

    public void onClick(MouseEvent mouseEvent) {
        int x = (int) ((mouseEvent.getX()) / (int) ((canvas.getWidth()) / 8));
        int y = (int) ((mouseEvent.getY()) / (int) ((canvas.getWidth()) / 8));

        if (activeFigure != null) {
            synchronized (this) {
                Signalization signal = board.getLastSignal();
                String tmp = null;
                if ((y == 0 || y == 7) && board.getState().getPieceOnPlace(activeFigure.getX(), activeFigure.getY()) instanceof Pawn) {
                    if (legalMoves.stream().anyMatch(coordinates -> coordinates.getX() == x && coordinates.getY() == y))
                        tmp = choosePromotion();
                }
                board.makeMove(activeFigure.getX(), activeFigure.getY(), x, y, tmp);
                activeFigure = null;
                if (board.getLastSignal() == Signalization.NOPIECE) {
                    board.setLastSignal(signal);
                    drawingFunctions.refreshBoard();
                    return;
                }

                if (timer == null) {
                    setTimer();
                }


                textMoves.setText(textMoves.getText() + board.getAllMoves().getLast() + " ");
                drawingFunctions.refreshBoard();
            }
        } else {
            drawingFunctions.refreshBoard();
            legalMoves = board.getLegalMoves(x, y);
            if (legalMoves == null)
                return;
            drawingFunctions.drawSelectRenctangle(x, y);
            legalMoves.forEach(coordinates -> drawingFunctions.drawLegalMovePoint(coordinates.getX(), coordinates.getY()));
            activeFigure = new Coordinates(x, y);
        }
    }
    @FXML
    private void changeSceneMenu() throws IOException {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Main.primaryStage.setX((bounds.getMaxX()-1000)/2);
        Main.primaryStage.setY((bounds.getMaxY()-700)/2);
        Main.primaryStage.setWidth(1000);
        Main.primaryStage.setHeight(700);

        
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }
}
