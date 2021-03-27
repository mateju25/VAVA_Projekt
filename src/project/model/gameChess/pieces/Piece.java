package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

public abstract class Piece {
    protected Boolean black;
    protected Image pic;

    public Piece(Boolean black) {
        this.black = black;
    }

    public void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
        state.getState()[finishX][finishY] = state.getPieceOnPlace(startX, startY);
        state.getState()[startX][startY] = null;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) instanceof Pawn)
                    ((Pawn) state.getPieceOnPlace(i, j)).setEnPasant(false);
            }
        }
    }

    public abstract ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y);

    public Image getPic() {
        return this.pic;
    }

    public Boolean getBlack() {
        return this.black;
    }

    protected boolean insertMove(ArrayList<Coordinates> list, GameState state, int x, int y) {
        if (state.getPieceOnPlace(x, y) == null)
            list.add(new Coordinates(x, y));
        else {
            if (state.getPieceOnPlace(x, y).getBlack() != this.getBlack())
                list.add(new Coordinates(x, y));
            return false;
        }
        return true;
    }

}
