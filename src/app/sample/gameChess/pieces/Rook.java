package app.sample.gameChess.pieces;

import app.sample.gameChess.Coordinates;
import app.sample.gameChess.GameState;

import java.util.ArrayList;

public class Rook extends Piece{
    public Rook(Boolean black, String name) {
        super(black, name);
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        //x doprava
        for (int i = x+1; i < 8; i++) {
            if (!(insertMove(result, state, i, y)))
                break;
        }
        //x dolava
        for (int i = x-1; i >=0; i--) {
            if (!(insertMove(result, state, i, y)))
                break;
        }
        //y dohora
        for (int i = y-1; i >=0; i--) {
            if (!(insertMove(result, state, x, i)))
                break;
        }
        //y dodola
        for (int i = y+1; i < 8; i++) {
            if (!(insertMove(result, state, x, i)))
                break;
        }
        return result;
    }
}
