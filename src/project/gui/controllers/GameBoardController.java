package project.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
    public Button giveup;
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
    public static Chessboard board = null;
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
        timer = null;
        giveup.setVisible(false);
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
                    resultWarning.setText("Súper sa vzdal\n Víťaz čierny 1-0 ");
                else
                    resultWarning.setText("Súper sa vzdal\n Víťaz biely 1-0 ");

                LoginConnection.getInstance().addPoints(SingleplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, MultiplayerController.use ? 1 : 0, 0, 0);
                LoginConnection.getInstance().loginUser(LoginConnection.getInstance().getActivePlayer().getName(), LoginConnection.getInstance().getActivePlayer().getPassword());
                break;
            }
        }
    }

    public void initialize() {
        timer = null;
        textMoves.setText("");
        giveup.setVisible(true);
        if (MultiplayerController.use)
            revenge.setVisible(false);
        Main.primaryStage.setMaximized(true);
        resultWarning.setVisible(false);
        canvas.setDisable(false);

        final boolean[] secondPLayerFirst;

        if (SingleplayerController.use) {
            title.setText(LoginConnection.getInstance().getActivePlayer().getName()+ " vs Stockfish úroveň " + SingleplayerController.level);
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
////
//        makeMoves("d2d4 d7d5 c2c4 e7e6 g1f3 f8b4 b1c3 b8c6 c4d5 e6d5 e2e4 d5e4 c1g5 g8f6 a2a3 b4c3 b2c3 e4f3 g5e3 c8f5 d1b3 f3g2 f1g2 e8g8 b3b7 c6a5 b7f3 f5e4 f3e4 f6e4 g2e4 a5b3 e1g1 d8h4 e4a8 f8a8 f2f3 h4h3 a1e1 a8e8 f1f2 e8e3 e1e3 h7h6 e3e8 g8h7 e8e2 b3c1 e2e1 c1d3 e1b1 d3f2 g1f2 h3h2 f2e3 h2d6 b1b2 d6a3 b2c2 a3e7 e3d3 e7f6 c2f2 f6f5 d3e3 f5g5 e3e2 a7a5 e2d3 a5a4 f2d2 a4a3 d2d1 a3a2 d1a1 g5g2 d3e3 g2b2 a1a2 b2a2 e3e4 a2c2 e4f4 c2c3 f4e4 c3c2 e4f4 c2f2 f4e4 c7c5 d4c5 f2c5 e4f4 g7g5 f4g4 f7f5 g4g3 f5f4 g3g4 c5g1 g4f5 h6h5 f5f6 h5h4 f6f5 h4h3 f5f6 h3h2 f6f5 h2h1q f5e4 g5g4 e4f5 g4f3 f5f4 f3f2");
//        ((Stockfish)secondPlayer).setMoves(new LinkedList<>(board.getAllMoves()));
        secondPlayerThread.setDaemon(true);
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
        window.getIcons().add(new Image("/project/gui/resources/pictures/graphics/ikonka.png"));
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
