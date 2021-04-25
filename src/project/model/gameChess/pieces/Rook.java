package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Figurka veza. Dedi funkcionalitu od vseobecnej triedy Piece
 */
public class Rook extends Piece{
    private boolean isMoved = false;
    public Rook(Boolean black, Coordinates coors) {
        super(black, coors);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackRook.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/WhiteRook.png"));
    }

    public boolean isMoved() {
        return isMoved;
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }

    /**
     * Vrati mozne pohyby pre vezu.
     * @param state
     * @return
     */
    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state) {
        ArrayList<Coordinates> result = new ArrayList<>();
        int x = coors.getX();
        int y = coors.getY();
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
