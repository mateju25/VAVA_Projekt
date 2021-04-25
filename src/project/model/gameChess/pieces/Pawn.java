package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;
import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Figurka pesiaka. Dedi funkcionalitu od vseobecnej triedy Piece
 */
public class Pawn extends Piece{
    private boolean isMoved = false;
    private boolean enPasant = false;

    public Pawn(Boolean black, Coordinates coors) {
        super(black, coors);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackPawn.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/WhitePawn.png"));
    }

    public void setEnPasant(boolean enPasant) {
        this.enPasant = enPasant;
    }

    /**
     * Vykona pohyb pesiaka
     * @param state
     * @param finishX
     * @param finishY
     */
    public void makeMove(GameState state, int finishX, int finishY) {
        super.makeMove(state, finishX, finishY);

        int minus = 1;
        if (state.isBlackCloser())
            minus *= -1;

        if (black)
            minus *= -1;

        if (state.getPieceOnPlace(finishX, finishY+minus) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY+minus)).enPasant && state.getPieceOnPlace(finishX, finishY+ minus).getBlack() != black)
            state.getState().remove(state.getPieceOnPlace(finishX, finishY + minus));

        isMoved = true;

        if (Math.abs(finishY - coors.getY()) == 2)
            enPasant = true;

        if (finishY == 0 || finishY == 7) {
            if (state.getPromotion() == null)
                return;
            state.getState().remove(state.getPieceOnPlace(finishX, finishY));
            switch (state.getPromotion()) {
                case "q":
                    state.getState().add(new Queen(this.black, new Coordinates(finishX, finishY)));
                    break;
                case "n":
                    state.getState().add(new Knight(this.black, new Coordinates(finishX, finishY)));
                    break;
                case "b":
                    state.getState().add(new Bishop(this.black, new Coordinates(finishX, finishY)));
                    break;
                case "r":
                    state.getState().add(new Rook(this.black, new Coordinates(finishX, finishY)));
                    break;
            }

        }

    }

    /**
     * Vrati mozne pohyby pre pesiaka.
     * @param state
     * @return
     */
    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state) {
        ArrayList<Coordinates> result = new ArrayList<>();
        int x = coors.getX();
        int y = coors.getY();
        if (y == 0 || y == 7)
            return result;
        int minus = 1;
        if (state.isBlackCloser())
            minus *= -1;
        if (!black)
            minus *= -1;


        if (state.getPieceOnPlace(x, y+ minus) == null) {
            result.add(new Coordinates(x, y+ minus));
            if (!isMoved)
                if ((y+2*minus >= 0 && y+2*minus <= 7) && state.getPieceOnPlace(x, y+2*minus) == null)
                    result.add(new Coordinates(x, y+2*minus));
        }
        if (x-1 >=0 && (state.getPieceOnPlace(x-1, y+ minus) != null && state.getPieceOnPlace(x-1, y+ minus).getBlack() != black))
            result.add(new Coordinates(x-1, y+ minus));
        if (x+1 <=7 && (state.getPieceOnPlace(x+1, y+ minus) != null && state.getPieceOnPlace(x+1, y+ minus).getBlack() != black))
            result.add(new Coordinates(x+1, y+ minus));

        if (x-1 >=0 && (y == 3 || y == 4) && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
            result.add(new Coordinates(x-1, y+ minus));
        if (x+1 <=7 && (y == 3 || y == 4) && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
            result.add(new Coordinates(x+1, y+ minus));

        return result;
    }
}
