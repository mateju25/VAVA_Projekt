package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

public class Rook extends Piece{
    public Rook(Boolean black)  {
        super(black);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/BlackRook.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/WhiteRook.png"));
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
