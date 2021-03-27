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

    private ArrayList<Coordinates> movesOfKing(GameState state, int x, int y) {
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
        return result;
    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = movesOfKing(state, x, y);


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ArrayList<Coordinates> restricted = new ArrayList<>();
                if (state.getPieceOnPlace(i, j) == null || state.getPieceOnPlace(i, j) == this)
                    continue;

                if (state.getPieceOnPlace(i, j).getBlack() != black) {
                    if (state.getPieceOnPlace(i, j) instanceof King) {
                        restricted = movesOfKing(state, i, j);
                    } else {
                        if (state.getPieceOnPlace(i, j) instanceof Pawn) {
                            if (state.getPieceOnPlace(i, j).getBlack()) {
                                if (i-1 >=0)
                                    restricted.add(new Coordinates(i-1, j+1));
                                if (i+1 <=7)
                                    restricted.add(new Coordinates(i+1, j+1));
                            } else {
                                if (i-1 >=0)
                                    restricted.add(new Coordinates(i-1, j-1));
                                if (i+1 <=7)
                                    restricted.add(new Coordinates(i+1, j-1));
                            }
                        } else {
                            restricted.addAll(state.getPieceOnPlace(i, j).getLegalMoves(state, i, j));
                        }
                    }
                    for (Coordinates coor :
                            restricted) {
                        result.removeIf(coorNew -> coor.getX() == coorNew.getX() && coor.getY() == coorNew.getY());
                    }
                }
            }
        }
        ArrayList<Coordinates> newResult = new ArrayList<>();
        newResult.addAll(result);
        for (Coordinates coor :
                result) {
            if (state.getPieceOnPlace(coor.getX(), coor.getY()) != null) {
                if (isGuarded(state, coor.getX(), coor.getY()))
                    newResult.remove(coor);
            }
        }
        return newResult;
    }

    private boolean isGuarded(GameState state, int x, int y) {
        Piece attacked = state.getPieceOnPlace(x, y);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((state.getPieceOnPlace(i, j) != null && state.getPieceOnPlace(i, j).getBlack()) == attacked.getBlack()) {
                    ArrayList<Coordinates> legalMoves = state.getPieceOnPlace(i, j).getLegalMoves(state, i, j);
                    for (Coordinates coor :
                            legalMoves) {
                        if (coor.getX() == x && coor.getY() == y)
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
