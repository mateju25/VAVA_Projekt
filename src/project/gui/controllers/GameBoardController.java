package project.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import project.gui.Main;
import project.model.GameParticipant;
import project.model.databaseSystem.MultiplayerConnection;
import project.model.gameChess.Chessboard;
import project.model.gameChess.Coordinates;
import project.model.gameChess.Signalization;
import project.model.gameChess.pieces.Pawn;
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
    private GameParticipant secondPlayer;

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
                        if (board.getState().isBlackCloser())
                            botTime = botTime.minusSeconds(1);
                        else
                            topTime = topTime.minusSeconds(1);
                    } else {
                        if (board.getState().isBlackCloser())
                            topTime = topTime.minusSeconds(1);
                        else
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

        final boolean[] computerFirst;

        if (SingleplayerController.use) {
            board = new Chessboard(SingleplayerController.blackSide);
            computerFirst = new boolean[]{SingleplayerController.blackSide};
            secondPlayer = Stockfish.getInstance(SingleplayerController.level);
            drawingFunctions = new DrawingFunctions(canvas, board);

            botTime = LocalTime.of(0, SingleplayerController.minutes, SingleplayerController.seconds);
            topTime = LocalTime.of(0, SingleplayerController.minutes, SingleplayerController.seconds);
            topTimerText.setText(topTime.format(DateTimeFormatter.ofPattern("mm:ss")));
            botTimerText.setText(botTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        }
        else
            if (MultiplayerController.use) {
                board = new Chessboard(MultiplayerController.blackSide);
                computerFirst = new boolean[]{MultiplayerController.blackSide};
                secondPlayer = MultiplayerConnection.getInstance();
                drawingFunctions = new DrawingFunctions(canvas, board);

                botTime = LocalTime.of(0, 5, 0);
                topTime = LocalTime.of(0, 5, 0);
                topTimerText.setText(topTime.format(DateTimeFormatter.ofPattern("mm:ss")));
                botTimerText.setText(botTime.format(DateTimeFormatter.ofPattern("mm:ss")));
            } else
            {
                computerFirst = new boolean[]{false};
            }

        Thread stockfishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String lastMove = board.getAllMoves().size() == 0 ? "" : board.getAllMoves().getLast();
                while (board.getLastSignal() != Signalization.CHECKMATE && board.getLastSignal() != Signalization.STALEMATE) {
                    // cakaj pokial aktualny hrac nezahra
                    while (lastMove.equals( board.getAllMoves().size() == 0 ? "" : board.getAllMoves().getLast())) {
                        if (computerFirst[0]) {
                            setTimer();
                            break;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    computerFirst[0] = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (this) {
                        String newMove = lastMove;
                        while (newMove.equals(lastMove)) {
                            if (secondPlayer instanceof Stockfish) {
                                newMove = secondPlayer.getLastMove();
                            } else {
                                newMove = secondPlayer.getLastMove();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (!newMove.equals(board.getAllMoves().size() == 0 ? "" : board.getAllMoves().getLast())) {
                            if (!newMove.contains("none"))
                                board.makeMove(newMove);
                            lastMove = board.getAllMoves().getLast();
                            textMoves.setText(textMoves.getText() + newMove + " ");
                            drawingFunctions.refreshBoard();
                        }
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
//
//        makeMoves("d2d4 e7e5 d4e5 d7d6 e5d6 d8d6 d1d6 f8d6 g1f3 g8f6 e2e3 e8g8 c1d2 c8g4 h2h3 g4f3 g2f3 b8c6 a2a3 a7a6 h1g1 b7b5 f3f4 f6e4 f1g2 a8e8 g2e4 e8e4 b1c3 e4e8 c3e2 d6f4 e2f4 c6d4 e1c1 d4f3 d2b4 f3g1 d1g1 e8d8 h3h4 f8e8 h4h5 h7h6 f4d3 d8c8 b2b3 c7c5 b4a5 c5c4 b3c4 c8c4 a5b4 e8c8 c2c3 g8h7 c1d2 g7g6 f2f3 g6g5 e3e4 f7f6 g1e1 h7g7 e4e5 g7f7 e5f6 f7f6 a3a4 b5a4 d2c2 a4a3 c2b3 a6a5 b4a5 c4c5 d3c5 c8c5 a5b4 c5b5 b3a2 b5b8 e1c1 b8a8 b4d6 f6e6 d6b4 e6e5 c1a1 e5f4 a2b3 f4f3 c3c4 g5g4 c4c5 g4g3 c5c6 g3g2 b4c5 a8a6 c6c7 a6c6 c5d6 f3f2 d6h2 f2f3 ");
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
            synchronized (this) {
                Signalization signal = board.getLastSignal();
                String tmp = null;
                if ((y == 0 || y == 7) && board.getState().getPieceOnPlace(activeFigure.getX(), activeFigure.getY()) instanceof Pawn) {
                    if (legalMoves.stream().anyMatch(coordinates -> coordinates.getX() == x && coordinates.getY() == y))
                        tmp = choosePromotion();
                }
                board.makeMove(activeFigure.getX(), activeFigure.getY(), x, y, tmp);
                if (!(secondPlayer instanceof Stockfish))
                    MultiplayerConnection.getInstance().makeMove(board.getAllMoves().getLast());
                else
                    ((Stockfish) secondPlayer).makeMove(board.getAllMoves().getLast());

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
