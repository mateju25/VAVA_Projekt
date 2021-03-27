package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

public class King extends Piece{
    public King(Boolean black) {
        super(black);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/BlackKing.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/WhiteKing.png"));
    }

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

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ArrayList<Coordinates> restricted = null;
                if (state.getPieceOnPlace(i, j) == null || state.getPieceOnPlace(i, j) instanceof King)
                    continue;
                if (state.getPieceOnPlace(i, j).getBlack() != black) {
                    restricted = state.getPieceOnPlace(i, j).getLegalMoves(state, i, j);

                    for (Coordinates coor :
                            restricted) {
                        result.removeIf(coorNew -> coor.getX() == coorNew.getX() && coor.getY() == coorNew.getY());
                    }
                }
            }
        }
        return result;
    }
}
