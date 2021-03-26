package project.model.gameChess;

import project.model.gameChess.pieces.Pawn;
import project.model.gameChess.pieces.Queen;

import java.util.ArrayList;

public class Chessboard {
    private GameState state;

    public Chessboard(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public ArrayList<Coordinates> getLegalMoves(int x, int y) {
        if (state.getPieceOnPlace(x,y) == null)
            return new ArrayList<>();
        else
            return state.getPieceOnPlace(x,y).getLegalMoves(state, x, y);
    }

    public void makeMove(int startX, int startY, int finishX, int finishY) {
        state.getPieceOnPlace(startX, startY).makeMove(state, startX, startY, finishX, finishY);
    }

//    public static void main(String[] args) {
//        Chessboard chessboard = new Chessboard(new GameState());
//        chessboard.getState().setNewStateStandardWhiteFiguresCloser();
//        chessboard.getState().drawState();
//
//        ArrayList<Coordinates> surs = chessboard.getState().getPieceOnPlace(7,6).getLegalMoves(chessboard.getState(), 7, 6);
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                if (chessboard.isInCoors(surs, j, i))
//                    System.out.print("_");
//                else
//                    System.out.print(" ");
//            }
//            System.out.print("\n");
//        }
//        for (Coordinates coor:
//             surs) {
//            System.out.println(coor.getX() + " " + coor.getY());
//        }
//    }

    public boolean isInCoors(ArrayList<Coordinates> surs, int x, int y) {
        for (Coordinates coor:
                surs) {
            if (coor.getX() == x && coor.getY() == y)
                return true;
        }
        return false;
    }
}
