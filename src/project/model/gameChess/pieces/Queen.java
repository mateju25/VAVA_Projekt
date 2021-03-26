package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

public class Queen extends Piece{
    public Queen(Boolean black)  {
        super(black);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/BlackQueen.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/WhiteQueen.png"));
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        Rook tempRook = new Rook(this.black);
        result.addAll(tempRook.getLegalMoves(state, x, y));
        Bishop tempBishop = new Bishop(this.black);
        result.addAll(tempBishop.getLegalMoves(state, x, y));
        return result;
    }
}
