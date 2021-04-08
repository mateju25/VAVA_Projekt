package project.model.gameChess;

import project.model.gameChess.pieces.*;

import java.util.ArrayList;
import java.util.Arrays;

public class GameState {
    private Piece[][] state = new Piece[8][8];
    private boolean blackCloser = false;
    private String promotion = null;

    GameState() {
        for(Piece[] array : state) Arrays.fill(array, null);
    }

    public boolean isBlackCloser() {
        return blackCloser;
    }

    public String isPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    void setBlackCloser(boolean blackCloser) {
        this.blackCloser = blackCloser;
    }

    GameState makeCopyFromActualGame() {
        GameState copyGame = new GameState();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getPieceOnPlace(i, j) != null) {
                    try {
                        copyGame.getState()[i][j] = (Piece) getPieceOnPlace(i, j).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return copyGame;
    }

    ArrayList<Coordinates> getLegalMoves(int x, int y) {
        ArrayList<Coordinates> legalMoves =  new ArrayList<>();
        if (getPieceOnPlace(x,y) == null)
            return legalMoves;
        else
            legalMoves = getPieceOnPlace(x,y).getLegalMoves(this, x, y);

        if (getPieceOnPlace(x,y) instanceof King && !((King) getPieceOnPlace(x,y)).isMoved()) {
            if (isChecked(this, getPieceOnPlace(x,y).getBlack()) == null &&
                    getPieceOnPlace(x+1,y) == null &&
                    getPieceOnPlace(x+2,y) == null &&
                    getPieceOnPlace(x+3,y) instanceof Rook &&
                    !((Rook) getPieceOnPlace(x+3,y)).isMoved() &&
                    getPieceOnPlace(x+3, y).getBlack() == getPieceOnPlace(x,y).getBlack() &&
                    !isSquareAttacked(this, x + 1, y, getPieceOnPlace(x, y).getBlack()) &&
                    !isSquareAttacked(this, x + 2, y, getPieceOnPlace(x, y).getBlack()))
                legalMoves.add(new Coordinates(x+2, y));
            if (isChecked(this, getPieceOnPlace(x,y).getBlack()) == null &&
                    getPieceOnPlace(x-1,y) == null &&
                    getPieceOnPlace(x-2,y) == null &&
                    getPieceOnPlace(x-3,y) == null &&
                    getPieceOnPlace(x-4,y) instanceof Rook &&
                    !((Rook) getPieceOnPlace(x-4,y)).isMoved() &&
                    getPieceOnPlace(x-4, y).getBlack() == getPieceOnPlace(x,y).getBlack() &&
                    !isSquareAttacked(this, x - 1, y, getPieceOnPlace(x, y).getBlack()) &&
                    !isSquareAttacked(this, x - 2, y, getPieceOnPlace(x, y).getBlack()) &&
                    !isSquareAttacked(this, x - 3, y, getPieceOnPlace(x, y).getBlack()))
                legalMoves.add(new Coordinates(x-2, y));
        }

        if ((isChecked(this, getPieceOnPlace(x,y).getBlack()) != null) && (isChecked(this, getPieceOnPlace(x,y).getBlack()).getBlack() != getPieceOnPlace(x,y).getBlack()))
            return legalMoves;
        ArrayList<Coordinates> newlegalMoves =  new ArrayList<>();
        if (legalMoves.size() == 0)
            return legalMoves;
        else {

            for (Coordinates coor :
                    legalMoves) {
                GameState temp = makeCopyFromActualGame();
                makeMove(temp, x, y, coor.getX(), coor.getY());
                Piece checking = isChecked(temp, getPieceOnPlace(x,y).getBlack());
                if (checking == null || checking.getBlack() != getPieceOnPlace(x,y).getBlack())
                    newlegalMoves.add(coor);
            }
        }
        return newlegalMoves;
    }

    void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
//        if (promotion == null) {
//            if (finishY == 0 || finishY == 7) {
//                if (state.getPieceOnPlace(startX, startY) instanceof Pawn) {
//                    promotion = new String("-1");
//                }
//            }
//        }
        state.getPieceOnPlace(startX, startY).makeMove(state, startX, startY, finishX, finishY);
        if (state.getPieceOnPlace(finishX, finishY) instanceof King) {
            ((King) state.getPieceOnPlace(finishX, finishY)).setMoved(true);
            if (startX-finishX==2) {
                state.getPieceOnPlace(startX-4, startY).makeMove(state, startX-4, startY, finishX+1, finishY);
            }
            if (startX-finishX==-2) {
                state.getPieceOnPlace(startX+3, startY).makeMove(state, startX+3, startY, finishX-1, finishY);
            }
        }
        if (state.getPieceOnPlace(finishX, finishY) instanceof Rook) {
            ((Rook) state.getPieceOnPlace(finishX, finishY)).setMoved(true);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) instanceof Pawn && (state.getPieceOnPlace(i, j) != state.getPieceOnPlace(finishX, finishY)))
                    ((Pawn) state.getPieceOnPlace(i, j)).setEnPasant(false);
            }
        }
    }

    public Piece getPieceOnPlace(int x, int y) {
        return state[x][y];
    }

    public Piece[][] getState() {
        return state;
    }

    void setNewStateStandardWhiteFiguresCloser() {
        //white
        for (byte i = 0; i < 8; i++)
            state[i][6] = new Pawn(false);
        state[0][7] = new Rook(false);
        state[7][7] = new Rook(false);
        state[1][7] = new Knight(false);
        state[6][7] = new Knight(false);
        state[2][7] = new Bishop(false);
        state[5][7] = new Bishop(false);
        state[3][7] = new Queen( false);
        state[4][7] = new King(false);

        //black
        for (byte i = 0; i < 8; i++)
            state[i][1] = new Pawn(true);
        state[0][0] = new Rook( true);
        state[7][0] = new Rook(true);
        state[1][0] = new Knight(true);
        state[6][0] = new Knight(true);
        state[2][0] = new Bishop(true);
        state[5][0] = new Bishop( true);
        state[3][0] = new Queen(true);
        state[4][0] = new King( true);
    }

    void setNewStateStandardBlackFiguresCloser() {
        //white
        for (byte i = 0; i < 8; i++)
            state[i][6] = new Pawn(true);
        state[0][7] = new Rook(true);
        state[7][7] = new Rook(true);
        state[1][7] = new Knight(true);
        state[6][7] = new Knight(true);
        state[2][7] = new Bishop(true);
        state[5][7] = new Bishop(true);
        state[3][7] = new Queen( true);
        state[4][7] = new King(true);

        //black
        for (byte i = 0; i < 8; i++)
            state[i][1] = new Pawn(false);
        state[0][0] = new Rook( false);
        state[7][0] = new Rook(false);
        state[1][0] = new Knight(false);
        state[6][0] = new Knight(false);
        state[2][0] = new Bishop(false);
        state[5][0] = new Bishop( false);
        state[3][0] = new Queen(false);
        state[4][0] = new King( false);
    }

    public Coordinates whereIsThis(Piece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.getPieceOnPlace(i, j) == piece)
                    return new Coordinates(i, j);
            }
        }
        return null;
    }

    public Piece isChecked(GameState state, boolean black) {
        Coordinates whiteKing = null;
        Coordinates blackKing = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) instanceof King)
                    if (state.getPieceOnPlace(i, j).getBlack()) {
                        blackKing = new Coordinates(i, j);
                    }
                    else {
                        whiteKing = new Coordinates(i, j);
                    }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) != null) {
                    ArrayList<Coordinates> coors = state.getPieceOnPlace(i, j).getLegalMoves(state, i, j);
                    for (Coordinates coor:
                            coors) {
                        if (coor.getY() == whiteKing.getY() && coor.getX() == whiteKing.getX() && !black)
                            return state.getPieceOnPlace(whiteKing.getX(), whiteKing.getY());
                        if (coor.getY() == blackKing.getY() && coor.getX() == blackKing.getX() && black)
                            return state.getPieceOnPlace(blackKing.getX(), blackKing.getY());
                    }
                }
            }
        }
        return null;
    }

    public Piece isCheckMated(GameState state, boolean black) {
        if (isChecked(state, black) != null) {
            Coordinates whiteKing = null;
            Coordinates blackKing = null;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (state.getPieceOnPlace(i, j) instanceof King)
                        if (state.getPieceOnPlace(i, j).getBlack()) {
                            blackKing = new Coordinates(i, j);
                        }
                        else {
                            whiteKing = new Coordinates(i, j);
                        }
                }
            }
            if (state.getLegalMoves(whiteKing.getX(), whiteKing.getY()).size() == 0) {
                if (!isAnyThereLegalMove(state, false))
                    return state.getPieceOnPlace(whiteKing.getX(), whiteKing.getY());
            }

            if (state.getLegalMoves(blackKing.getX(), blackKing.getY()).size() == 0)
                if (!isAnyThereLegalMove(state, true))
                    return state.getPieceOnPlace(blackKing.getX(), blackKing.getY());
        }
        return null;
    }

    boolean isAnyThereLegalMove(GameState state, boolean blackOnMove) {
        ArrayList<Coordinates> legalMoves =  new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) != null && state.getPieceOnPlace(i, j).getBlack() == blackOnMove) {
                    if (state.getLegalMoves(i, j).size() != 0)
                        return true;
                }
            }
        }
        return false;
    }

    boolean isSquareAttacked(GameState state, int x, int y, boolean black) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) != null) {
                    if (state.getPieceOnPlace(i, j).getBlack() != black) {
                        for (Coordinates coor : state.getPieceOnPlace(i, j).getLegalMoves(state, i, j)) {
                            if (coor.getX() == x && coor.getY() == y)
                                return true;
                        }
                    }
                }

            }
        }
        return false;
    }

}

