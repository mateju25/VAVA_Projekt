package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

public class Pawn extends Piece{
    private boolean isMoved = false;
    private boolean enPasant = false;
    public Pawn(Boolean black)  {
        super(black);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/BlackPawn.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/WhitePawn.png"));
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }

    public void setEnPasant(boolean enPasant) {
        this.enPasant = enPasant;
    }


    public void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
        state.getState()[finishX][finishY] = state.getPieceOnPlace(startX, startY);
        state.getState()[startX][startY] = null;

        if (black) {
            if (state.getPieceOnPlace(finishX, finishY-1) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY-1)).enPasant && state.getPieceOnPlace(finishX, finishY-1).getBlack() != black)
                state.getState()[finishX][finishY -1] = null;
        } else {
            if (state.getPieceOnPlace(finishX, finishY+1) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY+1)).enPasant && state.getPieceOnPlace(finishX, finishY+1).getBlack() != black)
                state.getState()[finishX][finishY +1] = null;
        }

        isMoved = true;

        if (Math.abs(finishY - startY) == 2)
            enPasant = true;

        if (finishY == 0 || finishY == 7)
            state.getState()[finishX][finishY] = new Queen(black);
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        if (y == 0 || y == 7)
            return result;
        if (black) {
            if (state.getPieceOnPlace(x, y+1) == null) {
                result.add(new Coordinates(x, y+1));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y+2) == null)
                        result.add(new Coordinates(x, y+2));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y+1) != null && state.getPieceOnPlace(x-1, y+1).getBlack() != black))
                result.add(new Coordinates(x-1, y+1));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y+1) != null && state.getPieceOnPlace(x+1, y+1).getBlack() != black))
                result.add(new Coordinates(x+1, y+1));

            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
                result.add(new Coordinates(x-1, y+1));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
                result.add(new Coordinates(x+1, y+1));
        } else {
            if (state.getPieceOnPlace(x, y-1) == null) {
                result.add(new Coordinates(x, y-1));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y-2) == null)
                        result.add(new Coordinates(x, y-2));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y-1) != null && state.getPieceOnPlace(x-1, y-1).getBlack() != black))
                result.add(new Coordinates(x-1, y-1));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y-1) != null && state.getPieceOnPlace(x+1, y-1).getBlack() != black))
                result.add(new Coordinates(x+1, y-1));

            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
                result.add(new Coordinates(x-1, y-1));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
                result.add(new Coordinates(x+1, y-1));
        }

        return result;
    }
}
