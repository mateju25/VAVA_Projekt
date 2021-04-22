package project.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import project.model.databaseSystem.LoginConnection;
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


public class GameBoardController {
    public Button revenge;
    @FXML private Label title;
    @FXML private Label resultWarning;
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
    private Thread stockfishThread;

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
                    if (MultiplayerController.use) {
                        if (MultiplayerController.host) {
                            MultiplayerConnection.getInstance().setTimers(botTime, topTime);
                        } else {
                            LocalTime[] times = MultiplayerConnection.getInstance().getTimes();
                            topTime = times[0];
                            botTime = times[1];
                        }
                    }
                    Platform.runLater(() -> {
                        topTimerText.setText(topTime.format(DateTimeFormatter.ofPattern("mm:ss")));
                        botTimerText.setText(botTime.format(DateTimeFormatter.ofPattern("mm:ss")));
                    });
                }
                else {
                    if (!stop) {
                        board.setLastSignal(Signalization.NOTIME);
                        timer.cancel();
                        Platform.runLater(() -> setResult());
                    }
                }
            }
        }, 1000,1000);
    }

    public void exitEverything() {
        stop = true;
        try {
            stockfishThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (timer != null) timer.cancel();
        canvas.setDisable(true);
    }

    public synchronized void setResult() {
        switch (board.getLastSignal()) {
            case CHECKMATE:
                exitEverything();
                resultWarning.setVisible(true);
                drawingFunctions.drawMateRecntangle(board.getState().whereIsThis(board.getState().isChecked(board.getState(), board.isBlackTurn())));

                int addPC = SingleplayerController.use ? 1 : 0;
                int addPlayer = MultiplayerController.use ? 1 : 0;
                int addWin = 0;
                int addLose = 0;

                if (board.getState().isBlackCloser() && board.isWhiteCheckmated()) {
                    resultWarning.setText("MAT\nVíťaz čierny 1-0");
                    addWin = MultiplayerController.use ? 1 : 0;
                }
                if (board.getState().isBlackCloser() && board.isBlackCheckmated()) {
                    resultWarning.setText("MAT\nVíťaz biely 0-1");
                    addLose = MultiplayerController.use ? 1 : 0;
                }
                if (!board.getState().isBlackCloser() && board.isBlackCheckmated()) {
                    resultWarning.setText("MAT\nVíťaz biely 1-0");
                    addWin = MultiplayerController.use ? 1 : 0;
                }
                if (!board.getState().isBlackCloser() && board.isWhiteCheckmated()) {
                    resultWarning.setText("MAT\nVíťaz čierny 0-1");
                    addLose = MultiplayerController.use ? 1 : 0;
                }
                LoginConnection.getInstance().addPoints(addPC, addPlayer, addWin, 0, addLose);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            case STALEMATE:
                exitEverything();
                resultWarning.setVisible(true);
                resultWarning.setText("PAT\n Remíza 1/2-1/2 ");
                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, 0, 1, 0);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            case NOTIME:
                exitEverything();
                resultWarning.setVisible(true);

                addPC = SingleplayerController.use ? 1 : 0;
                addPlayer = MultiplayerController.use ? 1 : 0;
                addWin = 0;
                addLose = 0;


                if (board.getState().isBlackCloser() && botTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Došiel čas\nVíťaz čierny 1-0");
                    addWin = MultiplayerController.use ? 1 : 0;
                }
                if (board.getState().isBlackCloser() && topTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Došiel čas\nVíťaz biely 0-1");
                    addLose = MultiplayerController.use ? 1 : 0;
                }
                if (!board.getState().isBlackCloser() && topTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Došiel čas\nVíťaz čierny 0-1");
                    addLose = MultiplayerController.use ? 1 : 0;
                }
                if (!board.getState().isBlackCloser() && botTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Došiel čas\nVíťaz biely 1-0");
                    addWin = MultiplayerController.use ? 1 : 0;
                }
                LoginConnection.getInstance().addPoints(addPC, addPlayer, addWin, 0, addLose);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            case GIVEUPME:
                exitEverything();
                resultWarning.setVisible(true);
                if (board.getState().isBlackCloser())
                    resultWarning.setText("Vzdal si sa\n Víťaz biely 0-1 ");
                else
                    resultWarning.setText("Vzdal si sa\n Víťaz čierny 0-1 ");
                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, 0, 0, MultiplayerController.use ? 1 : 0);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            case GIVEUPOTHER:
                exitEverything();
                resultWarning.setVisible(true);
                if (board.getState().isBlackCloser())
                    resultWarning.setText("Vzdal sa súper\n Víťaz čierny 1-0 ");
                else
                    resultWarning.setText("Vzdal sa súper\n Víťaz biely 1-0 ");
                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, 0, 0);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
        }
    }

    public void initialize() {
        revenge.setVisible(false);
        title.setText(LoginConnection.getInstance().getActivePlayer().getName()+ " vs Stockfish úrovne " + String.valueOf(SingleplayerController.level));
        Main.primaryStage.setMaximized(true);
        resultWarning.setVisible(false);
        canvas.setDisable(false);

        final boolean[] computerFirst;

        if (SingleplayerController.use) {
            board = new Chessboard(SingleplayerController.blackSide);
            computerFirst = new boolean[]{SingleplayerController.blackSide};
            secondPlayer = Stockfish.getInstance(SingleplayerController.level);
            drawingFunctions = new DrawingFunctions(canvas, board);

            botTime = LocalTime.of(0, SingleplayerController.minutes, SingleplayerController.seconds);
            topTime = LocalTime.of(0, SingleplayerController.minutes, SingleplayerController.seconds);
        }
        else {
                board = new Chessboard(MultiplayerController.blackSide);
                computerFirst = new boolean[]{MultiplayerController.blackSide};
                secondPlayer = MultiplayerConnection.getInstance();
                drawingFunctions = new DrawingFunctions(canvas, board);

                LocalTime[] times = MultiplayerConnection.getInstance().getTimes();
                topTime = times[0];
                botTime = times[1];
        }

        MultiplayerController.minutes = botTime.getMinute();
        MultiplayerController.seconds = botTime.getSecond();

        topTimerText.setText(topTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        botTimerText.setText(botTime.format(DateTimeFormatter.ofPattern("mm:ss")));

        stockfishThread = new Thread(new Runnable() {
            @Override
            public void run() {

                String lastMove = board.getLastMove();
                OUTER:
                while (!stop && board.getLastSignal() != Signalization.CHECKMATE
                        && board.getLastSignal() != Signalization.STALEMATE
                        && board.getLastSignal() != Signalization.NOTIME) {
                    // cakaj pokial aktualny hrac nezahra
                    while (!stop && lastMove.equals(board.getLastMove())) {
                        if (MultiplayerController.use && MultiplayerConnection.getInstance().isGiveUp()) {
                            board.setLastSignal(Signalization.GIVEUPOTHER);
                            Platform.runLater(() -> setResult());
                            return;
                        }
                        if (computerFirst[0]) {
                            break;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (stop)
                        return;
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    synchronized (this) {
                        String newMove = lastMove;
                        while (!stop && newMove.equals(lastMove)) {
                            if (MultiplayerController.use && MultiplayerConnection.getInstance().isGiveUp()) {
                                board.setLastSignal(Signalization.GIVEUPOTHER);
                                Platform.runLater(() -> setResult());
                                return;
                            }
                            if (board.getLastSignal() == Signalization.NOTIME)
                                break OUTER;
                            newMove = secondPlayer.getLastMove();
                            if (computerFirst[0]) {
                                if (secondPlayer instanceof  Stockfish) {
                                    newMove = ((Stockfish) secondPlayer).makeBestMove();
                                    break;
                                }
                            }
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        computerFirst[0] = false;
                        if (secondPlayer instanceof  Stockfish || !newMove.equals(board.getLastMove())) {
                            if (!newMove.contains("none")) {
                                if (stop)
                                    return;
                                board.makeMove(newMove);
                                if (timer == null) {
                                    setTimer();
                                }
                            }
                            lastMove = board.getAllMoves().getLast();
                            textMoves.setText(textMoves.getText() + newMove + "  ");


                            drawingFunctions.refreshBoard();
                        }
                    }
                    Platform.runLater(() -> {
                        if (board.getLastSignal() == Signalization.CHECK)
                            drawingFunctions.drawCheckRectangle(board.getState().whereIsThis(board.getState().isChecked(board.getState(), board.isBlackTurn())));
                        else
                            setResult();
                    });
                }
                stop = true;
            }
        });
//
//        makeMoves("d2d3 e7e5 c2c4 c7c6 b2b4 d7d5 c4d5 c6d5 c1a3 b8c6 b1c3 d5d4 c3e4 f7f5 e4d2 g8f6 d1b3 b7b5 g1f3 f6d5");
//        ((Stockfish)secondPlayer).setMoves(new LinkedList<>(board.getAllMoves()));
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

                if (!(secondPlayer instanceof Stockfish))
                    MultiplayerConnection.getInstance().makeMove(board.getAllMoves().getLast());
                else {
                    ((Stockfish) secondPlayer).makeMove(board.getAllMoves().getLast());
                    ((Stockfish)secondPlayer).makeBestMove();
                }

                if (timer == null) {
                    setTimer();
                }


                textMoves.setText(textMoves.getText() + board.getAllMoves().getLast() + "  ");
                drawingFunctions.refreshBoard();
            }
        } else {
            if (board.getState().getPieceOnPlace(x, y) != null && board.getState().getPieceOnPlace(x, y).getBlack() == board.getState().isBlackCloser()) {
                drawingFunctions.refreshBoard();
                legalMoves = board.getLegalMoves(x, y);
                if (legalMoves == null)
                    return;
                drawingFunctions.drawSelectRenctangle(x, y);
                legalMoves.forEach(coordinates -> drawingFunctions.drawLegalMovePoint(coordinates.getX(), coordinates.getY()));
                activeFigure = new Coordinates(x, y);
            }
        }
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        exitEverything();
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
        Main.primaryStage.setMaximized(false);
    }

    public void giveUp(ActionEvent actionEvent) {
        if (SingleplayerController.use) {
            board.setLastSignal(Signalization.GIVEUPME);
            setResult();
        }
        if (MultiplayerController.use) {
            board.setLastSignal(Signalization.GIVEUPME);
            setResult();
            MultiplayerConnection.getInstance().setGiveUp(true);
        }
    }

    public void revenge(ActionEvent actionEvent) {
        if (stop) {
            if (SingleplayerController.use) {
                exitEverything();
                SingleplayerController.blackSide = !SingleplayerController.blackSide;
                initialize();
            }
            if (!MultiplayerConnection.getInstance().getLastMove().equals("")) {
                if (MultiplayerController.use) {
                    exitEverything();
                    MultiplayerController.blackSide = !MultiplayerController.blackSide;
                    MultiplayerConnection.getInstance().revengeGame(MultiplayerController.blackSide, LocalTime.of(0, MultiplayerController.minutes, MultiplayerController.seconds));
                    initialize();
                }
            } else {
                exitEverything();
                MultiplayerController.blackSide = !MultiplayerController.blackSide;
                initialize();
            }
        }
    }
}
