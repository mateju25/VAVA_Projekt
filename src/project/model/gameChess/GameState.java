package project.model.gameChess;

import project.model.gameChess.pieces.*;

import java.util.ArrayList;

public class GameState {
    private Piece[][] state;

    public GameState() {
        this.state = new Piece[8][8];
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                this.state[i][j] = null;
            }
        }
    }

    public GameState makeCopyFromActualGame() {
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

    public ArrayList<Coordinates> getLegalMoves(int x, int y) {
        ArrayList<Coordinates> legalMoves =  null;
        if (getPieceOnPlace(x,y) == null)
            return null;
        else
            legalMoves = getPieceOnPlace(x,y).getLegalMoves(this, x, y);

        if (isChecked(this) != null) {
            ArrayList<Coordinates> newlegalMoves =  new ArrayList<>();
            if (legalMoves.size() == 0)
                return null;
            else {

                for (Coordinates coor :
                        legalMoves) {
                    GameState temp = makeCopyFromActualGame();
                    makeMove(temp, x, y, coor.getX(), coor.getY());
                    Piece checking = isChecked(temp);
                    if (checking == null)
                        newlegalMoves.add(coor);
                }
            }
            return newlegalMoves;
        }
        return legalMoves;
    }

    public void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
        state.getPieceOnPlace(startX, startY).makeMove(state, startX, startY, finishX, finishY);
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

   public void setNewStateStandardWhiteFiguresCloser() {
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


    public Piece isChecked(GameState state) {
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
                        if (coor.getY() == whiteKing.getY() && coor.getX() == whiteKing.getX())
                            return state.getPieceOnPlace(whiteKing.getX(), whiteKing.getY());
                        if (coor.getY() == blackKing.getY() && coor.getX() == blackKing.getX())
                            return state.getPieceOnPlace(blackKing.getX(), blackKing.getY());
                    }
                }
            }
        }
        return null;
    }

    public Piece isCheckMated(GameState state) {
        if (isChecked(state) != null) {
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
            if (state.getLegalMoves(whiteKing.getX(), whiteKing.getY()) == null)
                return state.getPieceOnPlace(whiteKing.getX(), whiteKing.getY());
            if (state.getLegalMoves(blackKing.getX(), blackKing.getY()) == null)
                return state.getPieceOnPlace(blackKing.getX(), blackKing.getY());
        }
        return null;
    }


}

