package app.sample.gameChess.pieces;

import app.sample.gameChess.Coordinates;
import app.sample.gameChess.GameState;

import java.util.ArrayList;

public abstract class Piece {
    protected Boolean black;
    protected String name;

    public Piece(Boolean black, String name) {
        this.black = black;
        this.name = name;
    }

    public abstract ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y);

    public String getName() {
        return this.name;
    }

    public Boolean getBlack() {
        return this.black;
    }

    protected boolean insertMove(ArrayList<Coordinates> list, GameState state, int x, int y) {
        if (state.getPieceOnPlace(x, y) == null)
            list.add(new Coordinates(x, y));
        else {
            if (state.getPieceOnPlace(x, y).getBlack() != this.getBlack())
                list.add(new Coordinates(x, y));
            return false;
        }
        return true;
    }

}
