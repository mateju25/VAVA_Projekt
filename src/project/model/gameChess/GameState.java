package project.model.gameChess;

import project.model.gameChess.pieces.*;

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

        state[4][3] = new Knight( true);
    }
}

