package project.model.gameChess;

import java.util.ArrayList;

public class Chessboard {
    private GameState state;
    private ArrayList<String> allMoves = new ArrayList<>();
    private boolean blackTurn = false;

    public Chessboard(boolean blackCloser) {
        this.state = new GameState();
        if (blackCloser)
            this.state.setNewStateStandardBlackFiguresCloser();
        else
            this.state.setNewStateStandardWhiteFiguresCloser();
        this.state.setBlackCloser(blackCloser);
    }

    public boolean isBlackCloser() {
        return state.isBlackCloser();
    }

    public GameState getState() {
        return state;
    }

    public ArrayList<String> getAllMoves() {
        return allMoves;
    }

    public boolean isBlackTurn() {
        return blackTurn;
    }

    public ArrayList<Coordinates> getLegalMoves(int x, int y) {
        if (state.getPieceOnPlace(x,y).getBlack() && !blackTurn)
            return null;

        if (!state.getPieceOnPlace(x,y).getBlack() && blackTurn)
            return null;

        return state.getLegalMoves(x, y);
    }

    public void makeMove(int startX, int startY, int finishX, int finishY) {
        if (state.getPieceOnPlace(startX,startY) == null)
            return;
        if (state.getPieceOnPlace(startX,startY).getBlack() && !blackTurn)
            return;
        if (!state.getPieceOnPlace(startX,startY).getBlack() && blackTurn)
            return;

        state.makeMove(state, startX, startY, finishX, finishY);

        blackTurn = !blackTurn;

        if (state.isChecked(state) != null)
            System.out.println("SACH");
        if (state.isCheckMated(state) != null)
            System.out.println("MAT");

        allMoves.add(String.valueOf((char) ((char) startX + 97)) + String.valueOf(startY+1) + String.valueOf((char) ((char) finishX + 97)) + String.valueOf(finishY+1));
    }

}
