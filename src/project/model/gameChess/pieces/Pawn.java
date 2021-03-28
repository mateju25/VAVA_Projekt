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

        int minus = 1;
        if (state.isBlackCloser())
            minus = -1;

        if (black) {
            if (state.getPieceOnPlace(finishX, finishY-1*minus) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY-1*minus)).enPasant && state.getPieceOnPlace(finishX, finishY-1*minus).getBlack() != black)
                state.getState()[finishX][finishY -1*minus] = null;
        } else {
            if (state.getPieceOnPlace(finishX, finishY+1) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY+1*minus)).enPasant && state.getPieceOnPlace(finishX, finishY+1*minus).getBlack() != black)
                state.getState()[finishX][finishY +1*minus] = null;
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
        int minus = 1;
        if (state.isBlackCloser())
            minus = -1;
        if (black) {
            if (state.getPieceOnPlace(x, y+1*minus) == null) {
                result.add(new Coordinates(x, y+1*minus));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y+2*minus) == null)
                        result.add(new Coordinates(x, y+2*minus));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y+1*minus) != null && state.getPieceOnPlace(x-1, y+1*minus).getBlack() != black))
                result.add(new Coordinates(x-1, y+1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y+1*minus) != null && state.getPieceOnPlace(x+1, y+1*minus).getBlack() != black))
                result.add(new Coordinates(x+1, y+1*minus));

            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
                result.add(new Coordinates(x-1, y+1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
                result.add(new Coordinates(x+1, y+1*minus));
        } else {
            if (state.getPieceOnPlace(x, y-1*minus) == null) {
                result.add(new Coordinates(x, y-1*minus));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y-2*minus) == null)
                        result.add(new Coordinates(x, y-2*minus));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y-1*minus) != null && state.getPieceOnPlace(x-1, y-1*minus).getBlack() != black))
                result.add(new Coordinates(x-1, y-1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y-1*minus) != null && state.getPieceOnPlace(x+1, y-1*minus).getBlack() != black))
                result.add(new Coordinates(x+1, y-1*minus));

            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
                result.add(new Coordinates(x-1, y-1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
                result.add(new Coordinates(x+1, y-1*minus));
        }

        return result;
    }
}
