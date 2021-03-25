package app.sample.gameChess.pieces;

import app.sample.gameChess.Coordinates;
import app.sample.gameChess.GameState;

import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(Boolean black, String name) {
        super(black, name);
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        if (x+2 <= 7 && x >= 0 && y <= 7 && y-1 >=0)
            insertMove(result, state, x+2, y-1);
        if (x+2 <= 7 && x >= 0 && y+1 <= 7 && y >=0)
            insertMove(result, state, x+2, y+1);
        if (x <= 7 && x-2 >= 0 && y <= 7 && y-1 >=0)
            insertMove(result, state, x-2, y-1);
        if (x <= 7 && x-2 >= 0 && y+1 <= 7 && y >=0)
            insertMove(result, state, x-2, y+1);
        if (x+1 <= 7 && x >= 0 && y+2 <= 7 && y >=0)
            insertMove(result, state, x+1, y+2);
        if (x <= 7 && x-1 >= 0 && y+2 <= 7 && y >=0)
            insertMove(result, state, x-1, y+2);
        if (x+1 <= 7 && x >= 0 && y <= 7 && y-2 >=0)
            insertMove(result, state, x+1, y-2);
        if (x <= 7 && x-1 >= 0 && y <= 7 && y-2 >=0)
            insertMove(result, state, x-1, y-2);
        return result;
    }
}
