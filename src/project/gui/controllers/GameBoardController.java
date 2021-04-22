package project.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
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
    private boolean thisPlayerMoved = false;
    private GameParticipant secondPlayer;
    private Thread secondPlayerThread;

    @Deprecated
    public void makeMoves(String moves) {
        String[] arr = moves.split(" ");
        for (String str :
                arr) {
            board.makeMove(str);
        }
    }

    public void setTimer() {
        if (timer == null) {
            timer = new Timer();
        } else {
            return;
        }
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
                        timer.cancel();
                        board.setLastSignal(Signalization.NOTIME);
                        Platform.runLater(() -> handleSignalFromChessboard());
                    }
                }
            }
        }, 1000,1000);
    }

    public void exitEverything() {
        stop = true;
        try {
            secondPlayerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (timer != null) timer.cancel();
        canvas.setDisable(true);
    }

    public synchronized void handleSignalFromChessboard() {
        int addWin = 0;
        int addLose = 0;
        switch (board.getLastSignal()) {
            case CHECKMATE: {
                exitEverything();
                resultWarning.setVisible(true);

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

                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, addWin, 0, addLose);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            }

            case STALEMATE: {
                exitEverything();
                resultWarning.setVisible(true);
                resultWarning.setText("PAT\n Remíza 1/2-1/2 ");
                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, 0, 1, 0);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            }

            case NOTIME: {
                exitEverything();
                resultWarning.setVisible(true);

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

                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, addWin, 0, addLose);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            }

            case GIVEUPME: {
                exitEverything();
                resultWarning.setVisible(true);
                if (board.getState().isBlackCloser())
                    resultWarning.setText("Vzdal si sa\n Víťaz biely 0-1 ");
                else
                    resultWarning.setText("Vzdal si sa\n Víťaz čierny 0-1 ");

                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, 0, 0, MultiplayerController.use ? 1 : 0);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            }

            case GIVEUPOTHER: {
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
    }

    public void initialize() {
        timer = null;
        textMoves.setText("");
        revenge.setVisible(false);
        Main.primaryStage.setMaximized(true);
        resultWarning.setVisible(false);
        canvas.setDisable(false);

        final boolean[] secondPLayerFirst;

        if (SingleplayerController.use) {
            title.setText(LoginConnection.getInstance().getActivePlayer().getName()+ " vs Stockfish úrovne " + SingleplayerController.level);
            board = new Chessboard(SingleplayerController.blackSide);
            secondPLayerFirst = new boolean[]{SingleplayerController.blackSide};
            secondPlayer = Stockfish.getInstance(SingleplayerController.level);
            drawingFunctions = new DrawingFunctions(canvas, board);

            botTime = LocalTime.of(0, SingleplayerController.minutes, SingleplayerController.seconds);
            topTime = LocalTime.of(0, SingleplayerController.minutes, SingleplayerController.seconds);
        }
        else {
            title.setText("Multiplayer");
            board = new Chessboard(MultiplayerController.blackSide);
            secondPLayerFirst = new boolean[]{MultiplayerController.blackSide};
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

        secondPlayerThread = new Thread(new Runnable() {
            private void checkIfGiveUp() {
                if (MultiplayerController.use && MultiplayerConnection.getInstance().isGiveUp()) {
                    board.setLastSignal(Signalization.GIVEUPOTHER);
                    Platform.runLater(() -> handleSignalFromChessboard());
                }
            }

            private void sleepFor(int mil) {
                try {
                    Thread.sleep(mil);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {

                OUTER:
                while (!stop) {

                    // cakaj pokial aktualny hrac nezahra
                    while (!stop && !thisPlayerMoved) {
                        if (secondPLayerFirst[0])
                            break;
                        checkIfGiveUp();
                        sleepFor(500);

                        if (stop)
                            return;
                    }
                    thisPlayerMoved = false;
                    String lastMove = board.getLastMove();

                    if (secondPlayer instanceof Stockfish)
                        sleepFor(1300);


                    synchronized (this) {
                        String newMove = board.getLastMove();
                        while (!stop && newMove.equals(lastMove)) {

                            checkIfGiveUp();

                            if (stop)
                                return;

                            newMove = secondPlayer.getLastMove();

                            if (secondPLayerFirst[0] && secondPlayer instanceof  Stockfish) {
                                newMove = ((Stockfish) secondPlayer).makeBestMove();
                                break;
                            }

                            sleepFor(500);

                            if (stop)
                                return;
                        }

                        secondPLayerFirst[0] = false;

                        if (!newMove.contains("none") && newMove.length() >= 4) {
                            board.makeMove(newMove);
                            setTimer();
                            textMoves.setText(textMoves.getText() + newMove + "  ");
                            drawingFunctions.refreshBoard();
                        }
                    }
                    Platform.runLater(() -> handleSignalFromChessboard());
                }
                stop = true;
            }
        });
//
//        makeMoves("d2d4 e7e6 e2e4 d7d5 e4e5 c7c5 c2c3 b8c6 c1e3 d8b6 a2a3 b6b2 b1d2 b2c3 f1b5 g8e7 a1b1 c3a3 d4c5 e7f5 b5c6 b7c6 d2b3 a3b4 d1d2 b4e4 b1c1 e4g2 b3d4 g2h1 e1f1 f5d4 d2d4 c8a6 f1e1 h1g1 e1d2 g1h2 d2c2 h2h5 c2b1 a8b8 b1a2 h5e2 e3d2 e2b5");
//        ((Stockfish)secondPlayer).setMoves(new LinkedList<>(board.getAllMoves()));
        secondPlayerThread.start();
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

                thisPlayerMoved = true;
                setTimer();

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
            handleSignalFromChessboard();
        }
        if (MultiplayerController.use) {
            board.setLastSignal(Signalization.GIVEUPME);
            handleSignalFromChessboard();
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
            if (MultiplayerController.use) {
                if (!MultiplayerConnection.getInstance().getLastMove().equals("")) {
                    exitEverything();
                    MultiplayerController.blackSide = !MultiplayerController.blackSide;
                    MultiplayerConnection.getInstance().revengeGame(MultiplayerController.blackSide, LocalTime.of(0, MultiplayerController.minutes, MultiplayerController.seconds));
                    initialize();
                } else {
                    exitEverything();
                    MultiplayerController.blackSide = !MultiplayerController.blackSide;
                    initialize();
                }
            }
        }
    }
}
