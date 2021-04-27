package project.model.gameChess;

import project.model.gameChess.pieces.Bishop;
import project.model.gameChess.pieces.Knight;
import project.model.gameChess.pieces.Piece;
import project.model.gameChess.pieces.Queen;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Matej Delincak
 *
 * Trieda nadstavujuca logika sachovnice, ktora poskytuje riadenie figurok cez retazce tahov a zaroven zapuzdruje vnutornu logiku triedy GameState
 */
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

    /**
     * Vrati posledny tah v danej sekvencii tahov
     * @return
     */
    public String getLastMove() {
        return allMoves.size() == 0 ? "" : allMoves.getLast();
    }

    /**
     * Vrati posledny signal sachovnice.
     * @return
     */
    public Signalization getLastSignal() {
        return lastSignal;
    }

    public void setLastSignal(Signalization lastSignal) {
        this.lastSignal = lastSignal;
    }

    public boolean isBlackTurn() {
        return blackTurn;
    }

    /**
     * Vrati vsetky mozne tahy figurky na x, y suradniciach
     * @param x
     * @param y
     * @return
     */
    public ArrayList<Coordinates> getLegalMoves(int x, int y) {
        Piece piece = state.getPieceOnPlace(x,y);
        if (piece == null)
            return null;
        if (piece.getBlack() && !blackTurn)
            return null;
        if (!piece.getBlack() && blackTurn)
            return null;

        return state.getLegalMoves(piece);
    }

    /**
     * Vykona dany tah figurky. Zaroven vytvori signal, ktory neskor sluzi na komunikaciu s kontrolerom.
     * @param startX
     * @param startY
     * @param finishX
     * @param finishY
     * @param promotion
     */
    public void makeMove(int startX, int startY, int finishX, int finishY, String promotion) {
        if (state.getPieceOnPlace(startX, startY) == null) {
            lastSignal = Signalization.NO_PIECE;
            return;
        }
        if (state.getPieceOnPlace(startX, startY).getBlack() && !blackTurn) {
            lastSignal = Signalization.NO_PIECE;
            return;
        }
        if (!state.getPieceOnPlace(startX, startY).getBlack() && blackTurn) {
            lastSignal = Signalization.NO_PIECE;
            return;
        }

        if (getLegalMoves(startX, startY).stream().noneMatch(coordinates -> coordinates.getX() == finishX && coordinates.getY() == finishY)) {
            lastSignal = Signalization.NO_PIECE;
            return;
        }

        state.setPromotion(promotion);
        state.makeMove(state, startX, startY, finishX, finishY);

        blackTurn = !blackTurn;

        String s = "";
        if (state.getPromotion() != null) {
            Piece temp = state.getPieceOnPlace(finishX, finishY);
            if (temp instanceof Queen) {
                s = "q";
            } else if (temp instanceof Knight) {
                s = "n";
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

    /**
     * Pretazena funkcia makeMove so suradniciami. Z retazca tahu posklada pozadovane suradnice.
     * @param move
     */
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

    /**
     * Zisti, ci ma cierny sach-mat
     * @return
     */
    public boolean isBlackCheckmated() {
        Piece temp = state.isCheckMated(this.getState(), true);
        if (temp != null)
            return true;
        return false;
    }

    /**
     * Zisti, ci ma biely sach-mat
     * @return
     */
    public boolean isWhiteCheckmated() {
        Piece temp = state.isCheckMated(this.getState(), false);
        if (temp != null)
            return true;
        return false;
    }
}
