package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

public class Bishop extends Piece{
    public Bishop(Boolean black) {
        super(black);
        if (black)
             pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set1/BlackBishop.png"));
        else
             pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set1/WhiteBishop.png"));
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        ValueRange range = ValueRange.of(0, 7);
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
