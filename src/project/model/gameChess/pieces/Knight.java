package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Figurka kona. Dedi funkcionalitu od vseobecnej triedy Piece
 */
public class Knight extends Piece{
    public Knight(Boolean black, Coordinates coors) {
        super(black, coors);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackKnight.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber +"/WhiteKnight.png"));
    }

    /**
     * Vrati mozne pohyby pre kona.
     * @param state
     * @param x
     * @param y
     * @return
     */
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
