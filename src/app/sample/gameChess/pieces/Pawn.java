package app.sample.gameChess.pieces;

import app.sample.gameChess.Coordinates;
import app.sample.gameChess.GameState;

import java.util.ArrayList;

public class Pawn extends Piece{
    private boolean isMoved = false;
    public Pawn(Boolean black, String name) {
        super(black, name);
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        if (black) {
            if (state.getPieceOnPlace(x, y+1) == null) {
                result.add(new Coordinates(x, y+1));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y+2) == null)
                        result.add(new Coordinates(x, y+2));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y+1) == null || state.getPieceOnPlace(x-1, y+1).getBlack() != black))
                result.add(new Coordinates(x-1, y+1));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y+1) == null || state.getPieceOnPlace(x+1, y+1).getBlack() != black))
                result.add(new Coordinates(x+1, y+1));
        } else {
            if (state.getPieceOnPlace(x, y-1) == null) {
                result.add(new Coordinates(x, y-1));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y-2) == null)
                        result.add(new Coordinates(x, y-2));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y-1) == null || state.getPieceOnPlace(x-1, y-1).getBlack() != black))
                result.add(new Coordinates(x-1, y-1));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y-1) == null || state.getPieceOnPlace(x+1, y-1).getBlack() != black))
                result.add(new Coordinates(x+1, y-1));
        }

        return result;
    }
}
