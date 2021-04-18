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
    private boolean computerMove = false;
    private Signalization lastSignal = Signalization.NORMAL;

    public Chessboard(boolean blackCloser) {
        this.state = new GameState();
        if (blackCloser)
            this.state.setNewStateStandardBlackFiguresCloser();
        else
            this.state.setNewStateStandardWhiteFiguresCloser();
        this.state.setBlackCloser(blackCloser);
    }

    public GameState getState() {
        return state;
    }

    public LinkedList<String> getAllMoves() {
        return allMoves;
    }

    public Signalization getLastSignal() {
        return lastSignal;
    }

    public void setLastSignal(Signalization lastSignal) {
        this.lastSignal = lastSignal;
    }

    public boolean isBlackTurn() {
        return blackTurn;
    }

    public ArrayList<Coordinates> getLegalMoves(int x, int y) {
        if (state.getPieceOnPlace(x,y) == null)
            return null;
        if (state.getPieceOnPlace(x,y).getBlack() && !blackTurn)
            return null;
        if (!state.getPieceOnPlace(x,y).getBlack() && blackTurn)
            return null;

        return state.getLegalMoves(x, y);
    }

    public void makeMove(int startX, int startY, int finishX, int finishY, String promotion) {
        if (state.getPieceOnPlace(startX, startY) == null) {
            lastSignal = Signalization.NOPIECE;
            return;
        }
        if (state.getPieceOnPlace(startX, startY).getBlack() && !blackTurn) {
            lastSignal = Signalization.NOPIECE;
            return;
        }
        if (!state.getPieceOnPlace(startX, startY).getBlack() && blackTurn) {
            lastSignal = Signalization.NOPIECE;
            return;
        }

        if (getLegalMoves(startX, startY).stream().noneMatch(coordinates -> coordinates.getX() == finishX && coordinates.getY() == finishY)) {
            lastSignal = Signalization.NOPIECE;
            return;
        }

        state.setPromotion(promotion);
        state.makeMove(state, startX, startY, finishX, finishY);

        blackTurn = !blackTurn;

        String s = "";
        if (state.isPromotion() != null) {
            Piece temp = state.getPieceOnPlace(finishX, finishY);
            if (temp instanceof Queen) {
                s = "q";
            } else if (temp instanceof Knight) {
                s = "k";
            } else if (temp instanceof Bishop) {
                s = "b";
            } else {
                s = "r";
            }
        }
        if (state.isBlackCloser()) {
            allMoves.add(String.valueOf((char) ((char) 7-startX + 97)) + (startY + 1) + (char) ((char) 7-finishX + 97) + (finishY + 1) + s);
        } else {
            allMoves.add(String.valueOf((char) ((char) startX + 97)) + (7 - startY + 1) + (char) ((char) finishX + 97) + (7 - finishY + 1) + s);
        }



        if (state.isCheckMated(state, !state.getPieceOnPlace(finishX, finishY).getBlack()) != null) {
            lastSignal = Signalization.CHECKMATE;
            return;
        }
        if (state.isChecked(state, !state.getPieceOnPlace(finishX, finishY).getBlack()) != null) {
            lastSignal = Signalization.CHECK;
            return;
        }
        if (!state.isAnyThereLegalMove(state, !state.getPieceOnPlace(finishX, finishY).getBlack())) {
            lastSignal = Signalization.STALEMATE;
            return;
        }

        lastSignal =  Signalization.NORMAL;
    }

    public void makeMove(String move) {
        int startX , startY , finishX , finishY;
        if (state.isBlackCloser()) {
            startX = 7-(move.charAt(0) - 97);
            startY = Integer.parseInt(String.valueOf(move.charAt(1))) -1;
            finishX = 7-(move.charAt(2) - 97);
            finishY = Integer.parseInt(String.valueOf(move.charAt(3))) -1;
        } else {
            startX = move.charAt(0) - 97;
            startY = 7 - Integer.parseInt(String.valueOf(move.charAt(1))) + 1;
            finishX = move.charAt(2) - 97;
            finishY = 7 - Integer.parseInt(String.valueOf(move.charAt(3))) + 1;
        }
        String promotion = null;
        if (move.length() == 5)
            promotion = String.valueOf(move.charAt(4));

        computerMove = true;
        makeMove(startX, startY, finishX, finishY, promotion);
        computerMove = false;
    }

}
