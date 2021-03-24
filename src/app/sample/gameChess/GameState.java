package app.sample.gameChess;

import app.sample.gameChess.pieces.*;
import javafx.scene.layout.BackgroundSize;

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

    public void drawState() {
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                if (state[j][i] == null)
                    System.out.print(" ");
                else
                    System.out.print(state[j][i].getName());
            }
            System.out.print("\n");
        }
    }

    public void setNewStateStandardWhiteFiguresCloser() {
        //white
        for (byte i = 0; i < 8; i++)
            state[i][6] = new Pawn(false, "P");
        state[0][7] = new Rook(false, "V");
        state[7][7] = new Rook(false, "V");
        state[1][7] = new Knight(false, "K");
        state[6][7] = new Knight(false, "K");
        state[2][7] = new Bishop(false, "S");
        state[5][7] = new Bishop(false, "S");
        state[3][7] = new Queen( false, "Q");
        state[4][7] = new King(false, "K");

        //black
        for (byte i = 0; i < 8; i++)
            state[i][1] = new Pawn(true, "P");
        state[0][0] = new Rook( true, "V");
        state[7][0] = new Rook(true, "V");
        state[1][0] = new Knight(true, "K");
        state[6][0] = new Knight(true, "K");
        state[2][0] = new Bishop(true, "S");
        state[5][0] = new Bishop( true, "S");
        state[3][0] = new Queen(true, "Q");
        state[4][0] = new King( true, "K");

        state[6][5] = new Knight(false, "T");
    }
}

