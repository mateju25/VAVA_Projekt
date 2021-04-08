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

    private Coordinates activeFigure = null;
    private Stockfish computer = Stockfish.getInstance(1);


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
                while (board.getLastSignal() != Signalization.CHECKMATE) {
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

                    String newMove = computer.getBestMove(board.getAllMoves().getLast());
                    if (!computer.isCheckmate()) {
                        board.makeMove(newMove);
                        lastMove = board.getAllMoves().getLast();
                        textMoves.setText(textMoves.getText() + newMove + " ");
                    } else {
                        board.setLastSignal(Signalization.CHECKMATE);
                    }
                    drawingFunctions.refreshBoard();
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
                        }
                    });
                }
                stop = true;
            }
        });


//        makeMoves("e2e4 e7e5 g1f3 d7d5 d2d3 b8c6 f1e2 d5e4 d3e4 d8d1 e2d1 g8e7 e1g1 e7g6 a2a3 f7f6 c2c3 c8e6 b1d2 c6a5 b2b4 a5c4 d2c4 e6c4 f1e1 b7b5 a3a4 g6f4 a4b5 a8d8 c1d2 c4b5 a1a7 g7g5 a7c7 h7h6 d2f4 f8b4 f4d2 b4d6 c7b7 g5g4 f3h4 b5c6 b7b6 f6f5 h4f5 e8d7 c3c4 d6c5 b6a6 h6h5 d1a4 c6a4 a6a4 h5h4 d2g5 d8a8 a4a8 h8a8 g5h4 d7c6 h2h3 g4h3 g2h3 c6c7 h4g3 a8g8 g1h2 g8e8 e1d1 e8e6 f5g7 e6e7 g7f5 e7e8 h3h4 e8e6 h4h5 e6e8 h5h6 c7c6 h6h7 c5f2 g3f2 e8a8 f5e7 c6c7 e7g6 a8a2 h2g2 a2f2 g2f2 c7c8 ");
//        computer.setMoves(new LinkedList<>(board.getAllMoves()));
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
            Signalization signal = board.getLastSignal();
            String tmp = null;
            if ((y == 0 || y == 7) && board.getState().getPieceOnPlace(activeFigure.getX(), activeFigure.getY()) instanceof Pawn) {
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



            textMoves.setText(textMoves.getText() + board.getAllMoves().getLast()  + " " );
            drawingFunctions.refreshBoard();
        } else {
            drawingFunctions.refreshBoard();
            ArrayList<Coordinates> legalMoves = board.getLegalMoves(x, y);
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
