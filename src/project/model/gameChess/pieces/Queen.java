package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Figurka damy. Dedi funkcionalitu od vseobecnej triedy Piece
 */
public class Queen extends Piece{
    public Queen(Boolean black, Coordinates coors) {
        super(black, coors);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackQueen.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/WhiteQueen.png"));
    }

    /**
     * Vrati mozne pohyby pre damu.
     * @param state
     * @return
     */
    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state) {
        int x = coors.getX();
        int y = coors.getY();
        Rook tempRook = new Rook(this.black, coors);
        ArrayList<Coordinates> result = new ArrayList<>(tempRook.getLegalMoves(state));
        Bishop tempBishop = new Bishop(this.black, coors);
        result.addAll(tempBishop.getLegalMoves(state));
        return result;
    }
}
