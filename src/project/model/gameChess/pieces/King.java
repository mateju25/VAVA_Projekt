package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Figurka krala. Dedi funkcionalitu od vseobecnej triedy Piece
 */
public class King extends Piece{
    private boolean isMoved = false;
    public King(Boolean black, Coordinates coors) {
        super(black, coors);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackKing.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/WhiteKing.png"));
    }

    public boolean isMoved() {
        return isMoved;
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }

    /**
     * Vrati mozne pohyby pre krala.
     * @param state
     * @param x
     * @param y
     * @return
     */
    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        if (x+1 <= 7 && x >= 0 && y <= 7 && y >=0)
            insertMove(result, state, x+1, y);
        if (x+1 <= 7 && x >= 0 && y+1 <= 7 && y >=0)
            insertMove(result, state, x+1, y+1);
        if (x <= 7 && x >= 0 && y+1 <= 7 && y >=0)
            insertMove(result, state, x, y+1);
        if (x <= 7 && x-1 >= 0 && y+1 <= 7 && y >=0)
            insertMove(result, state, x-1, y+1);
        if (x <= 7 && x-1 >= 0 && y <= 7 && y >=0)
            insertMove(result, state, x-1, y);
        if (x <= 7 && x-1 >= 0 && y <= 7 && y-1 >=0)
            insertMove(result, state, x-1, y-1);
        if (x <= 7 && x >= 0 && y <= 7 && y-1 >=0)
            insertMove(result, state, x, y-1);
        if (x+1 <= 7 && x >= 0 && y <= 7 && y-1 >=0)
            insertMove(result, state, x+1, y-1);
        return result;
    }
}
