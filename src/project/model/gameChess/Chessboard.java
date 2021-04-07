package project.model.gameChess;

import project.model.gameChess.pieces.Bishop;
import project.model.gameChess.pieces.Knight;
import project.model.gameChess.pieces.Piece;
import project.model.gameChess.pieces.Queen;

import java.util.ArrayList;
import java.util.LinkedList;

public class Chessboard {
    private GameState state;
    private LinkedList<String> allMoves = new LinkedList<>();
    private boolean blackTurn = false;

    public Chessboard(boolean blackCloser) {
        this.state = new GameState();
        if (blackCloser)
            this.state.setNewStateStandardBlackFiguresCloser();
        else
            this.state.setNewStateStandardWhiteFiguresCloser();
        this.state.setBlackCloser(blackCloser);
    }

    public boolean isBlackCloser() {
        return state.isBlackCloser();
    }

    public GameState getState() {
        return state;
    }

    public LinkedList<String> getAllMoves() {
        return allMoves;
    }

    public boolean isBlackTurn() {
        return blackTurn;
    }

    public ArrayList<Coordinates> getLegalMoves(int x, int y) {
        if (state.getPieceOnPlace(x,y).getBlack() && !blackTurn)
            return null;

        if (!state.getPieceOnPlace(x,y).getBlack() && blackTurn)
            return null;

        return state.getLegalMoves(x, y);
    }

    public Signalization makeMove(int startX, int startY, int finishX, int finishY, String promotion) {
        if (state.getPieceOnPlace(startX,startY) == null)
            return Signalization.NORMAL;
        if (state.getPieceOnPlace(startX,startY).getBlack() && !blackTurn)
            return Signalization.NORMAL;
        if (!state.getPieceOnPlace(startX,startY).getBlack() && blackTurn)
            return Signalization.NORMAL;

        state.setPromotion(promotion);
        state.makeMove(state, startX, startY, finishX, finishY);

        blackTurn = !blackTurn;

        if (state.isPromotion() != null) {
            Piece temp = state.getPieceOnPlace(finishX, finishY);
            String s;
            if (temp instanceof Queen) {
                s = "q";
            } else if (temp instanceof Knight) {
                s = "k";
            } else if (temp instanceof Bishop) {
                s = "b";
            } else {
                s = "r";
            }
            allMoves.add(String.valueOf((char) ((char) startX + 97)) + (7 - startY + 1) + (char) ((char) finishX + 97) + (7 - finishY + 1) + s);
        }
        else
            allMoves.add(String.valueOf((char) ((char) startX + 97)) + (7 - startY + 1) + (char) ((char) finishX + 97) + (7 - finishY + 1));

        if (state.isCheckMated(state) != null)
            return Signalization.CHECKMATE;
        if (state.isChecked(state) != null)
            return Signalization.CHECK;

        return Signalization.NORMAL;
    }

    public void makeMove(String move) {
        int startX = move.charAt(0) - 97;
        int startY = 7- Integer.parseInt(String.valueOf(move.charAt(1))) + 1;
        int finishX = move.charAt(2) - 97;
        int finishY = 7 -Integer.parseInt(String.valueOf(move.charAt(3))) + 1;
        String promotion = null;
        if (move.length() == 5)
            promotion = String.valueOf(move.charAt(4));

//        blackTurn = !blackTurn;

        makeMove(startX, startY, finishX, finishY, promotion);
    }

}
