package app.sample.gameChess.pieces;

import app.sample.gameChess.Coordinates;
import app.sample.gameChess.GameState;

import java.util.ArrayList;

public class Queen extends Piece{
    public Queen(Boolean black, String name) {
        super(black, name);
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        Rook tempRook = new Rook(this.black, "T");
        result.addAll(tempRook.getLegalMoves(state, x, y));
        Bishop tempBishop = new Bishop(this.black, "T");
        result.addAll(tempBishop.getLegalMoves(state, x, y));
        return result;
    }
}
