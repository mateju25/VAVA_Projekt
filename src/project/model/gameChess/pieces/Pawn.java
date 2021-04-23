package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;
import java.util.ArrayList;

public class Pawn extends Piece{
    private boolean isMoved = false;
    private boolean enPasant = false;

    public Pawn(Boolean black)  {
        super(black);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/BlackPawn.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/figures/set" + SetNumber + "/WhitePawn.png"));
    }

    public void setEnPasant(boolean enPasant) {
        this.enPasant = enPasant;
    }

    public void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
        state.getState()[finishX][finishY] = state.getPieceOnPlace(startX, startY);
        state.getState()[startX][startY] = null;

        int minus = 1;
        if (state.isBlackCloser())
            minus *= -1;

        if (black)
            minus *= -1;

        if (state.getPieceOnPlace(finishX, finishY+minus) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY+minus)).enPasant && state.getPieceOnPlace(finishX, finishY+ minus).getBlack() != black)
            state.getState()[finishX][finishY + minus] = null;

        isMoved = true;

        if (Math.abs(finishY - startY) == 2)
            enPasant = true;

        if (finishY == 0 || finishY == 7) {
            if (state.isPromotion() == null)
                return;
            switch (state.isPromotion()) {
                case "q": state.getState()[finishX][finishY] = new Queen(this.black); break;
                case "k": state.getState()[finishX][finishY] = new Knight(this.black); break;
                case "b": state.getState()[finishX][finishY] = new Bishop(this.black); break;
                case "r": state.getState()[finishX][finishY] = new Rook(this.black); break;
            }

        }

    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
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
