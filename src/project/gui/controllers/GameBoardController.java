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
import project.model.databaseSystem.ChessPlayer;
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
                        board.setLastSignal(Signalization.NO_TIME);
                        Platform.runLater(() -> handleSignalFromChessboard());
                    }
                }
            }
        }, 1000,1000);
    }

    public void exitEverything() {
        stop = true;
        try {
            if (secondPlayerThread != null)
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
        if (board.getLastSignal() == Signalization.NO_PIECE ||
                board.getLastSignal() == Signalization.NORMAL ||
                board.getLastSignal() == Signalization.CHECK)
            return;
        ChessPlayer player = LoginConnection.getInstance().getActivePlayer();
        exitEverything();
        resultWarning.setVisible(true);
        switch (board.getLastSignal()) {
            case CHECKMATE: {
                if (board.getState().isBlackCloser() && board.isWhiteCheckmated()) {
                    resultWarning.setText("MAT\nV????az ??ierny 1-0");
                    player.setWins(player.getWins() + (MultiplayerController.use ? 1 : 0));
                }
                if (board.getState().isBlackCloser() && board.isBlackCheckmated()) {
                    resultWarning.setText("MAT\nV????az biely 0-1");
                    player.setLoses(player.getLoses() + (MultiplayerController.use ? 1 : 0));
                }
                if (!board.getState().isBlackCloser() && board.isBlackCheckmated()) {
                    resultWarning.setText("MAT\nV????az biely 1-0");
                    player.setWins(player.getWins() + (MultiplayerController.use ? 1 : 0));
                }
                if (!board.getState().isBlackCloser() && board.isWhiteCheckmated()) {
                    resultWarning.setText("MAT\nV????az ??ierny 0-1");
                    player.setLoses(player.getLoses() + (MultiplayerController.use ? 1 : 0));
                }

                break;
            }

            case STALEMATE: {
                resultWarning.setText("PAT\n Rem??za 1/2-1/2 ");

                player.setDraws(player.getDraws() + (MultiplayerController.use ? 1 : 0));

                break;
            }

            case NO_TIME: {
                if (board.getState().isBlackCloser() && botTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Do??iel ??as\nV????az ??ierny 1-0");
                    player.setWins(player.getWins() + (MultiplayerController.use ? 1 : 0));
                }
                if (board.getState().isBlackCloser() && topTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Do??iel ??as\nV????az biely 0-1");
                    player.setLoses(player.getLoses() + (MultiplayerController.use ? 1 : 0));
                }
                if (!board.getState().isBlackCloser() && topTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Do??iel ??as\nV????az ??ierny 0-1");
                    player.setLoses(player.getLoses() + (MultiplayerController.use ? 1 : 0));
                }
                if (!board.getState().isBlackCloser() && botTime.isAfter(LocalTime.of(0, 0, 0))) {
                    resultWarning.setText("Do??iel ??as\nV????az biely 1-0");
                    player.setWins(player.getWins() + (MultiplayerController.use ? 1 : 0));
                }

                break;
            }

            case GIVE_UP_ME: {
                if (board.getState().isBlackCloser())
                    resultWarning.setText("Vzdal si sa\n V????az biely 0-1 ");
                else
                    resultWarning.setText("Vzdal si sa\n V????az ??ierny 0-1 ");

                player.setLoses(player.getLoses() + (MultiplayerController.use ? 1 : 0));

                break;
            }

            case GIVE_UP_OTHER: {
                if (board.getState().isBlackCloser())
                    resultWarning.setText("S??per sa vzdal\n V????az ??ierny 1-0 ");
                else
                    resultWarning.setText("S??per sa vzdal\n V????az biely 1-0 ");

                player.setWins(player.getWins() + (MultiplayerController.use ? 1 : 0));

                break;
            }
        }

        if (MultiplayerController.use)
            player.setGamesVsPlayer(player.getGamesVsPlayer() + 1);
        if (SingleplayerController.use)
            player.setGamesVsPc(player.getGamesVsPc() + 1);

        LoginConnection.getInstance().saveUser();
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
            title.setText(LoginConnection.getInstance().getActivePlayer().getName()+ " vs Stockfish ??rove?? " + SingleplayerController.level);
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
                    board.setLastSignal(Signalization.GIVE_UP_OTHER);
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
//        makeMoves("e2e4 e7e5 g1f3 d7d6 c2c3 g8f6 d2d4 e5d4 c3d4 f6e4 d4d5 c7c6 d5c6 b8c6 f1b5 c8d7 f3d4 c6b4 a2a3 a7a6 b5d3 d8b6 d3e4 d6d5 a3b4 b6b4 b1c3 d5e4 d4c2 b4b3 c3e4");
//        ((Stockfish)secondPlayer).setMoves(new LinkedList<>(board.getAllMoves()));
//        board.setBlackTurn(false);
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
                if (board.getLastSignal() == Signalization.NO_PIECE) {
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
            board.setLastSignal(Signalization.GIVE_UP_ME);
            handleSignalFromChessboard();
        }
        if (MultiplayerController.use) {
            board.setLastSignal(Signalization.GIVE_UP_ME);
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
