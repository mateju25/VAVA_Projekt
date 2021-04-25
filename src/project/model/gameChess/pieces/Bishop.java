package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Figurka strelca. Dedi funkcionalitu od vseobecnej triedy Piece
 */
public class Bishop extends Piece{
    public Bishop(Boolean black, Coordinates coors) {
        super(black, coors);
        if (black)
             pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackBishop.png"));
        else
             pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/WhiteBishop.png"));
    }

    /**
     * Vrati mozne pohyby pre strelca.
     * @param state
     * @return
     */
    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state) {
        ArrayList<Coordinates> result = new ArrayList<>();
        ValueRange range = ValueRange.of(0, 7);
        int x = coors.getX();
        int y = coors.getY();
        //doprava hore
        int j = y-1;
        int i = x+1;
        while (Boolean.logicalAnd(range.isValidIntValue(i), range.isValidIntValue(j))) {
            if (!(insertMove(result, state, i++, j--)))
                break;
        }
        //doprava dole
        j = y+1;
        i = x+1;
        while (Boolean.logicalAnd(range.isValidIntValue(i), range.isValidIntValue(j))) {
            if (!(insertMove(result, state, i++, j++)))
                break;
        }
        //dolava hore
        j = y-1;
        i = x-1;
        while (Boolean.logicalAnd(range.isValidIntValue(i), range.isValidIntValue(j))) {
            if (!(insertMove(result, state, i--, j--)))
                break;
        }
        //dolava dole
        j = y+1;
        i = x-1;
        while (Boolean.logicalAnd(range.isValidIntValue(i), range.isValidIntValue(j))) {
            if (!(insertMove(result, state, i--, j++)))
                break;
        }
        return result;
    }
}
