package project.model.gameChess;

import project.model.gameChess.pieces.King;
import project.model.gameChess.pieces.Pawn;
import project.model.gameChess.pieces.Piece;

import java.util.ArrayList;

public class Chessboard {
    private GameState state;
    private boolean blackTurn = false;

    public Chessboard(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
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
    }

}
