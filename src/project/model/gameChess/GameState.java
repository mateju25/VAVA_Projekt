package project.model.gameChess;

import project.model.gameChess.pieces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Matej Delincak
 *
 * Trieda obsahujuca logiku sachovnice. Obsahuje rozne funkcie na tvorbu partii, ci ich manazement.
 */
public class GameState {
    private List<Piece> state = new ArrayList<>();
    private boolean blackCloser = false;
    private String promotion = null;

    /**
     * Vrati hodnotu, ci v danej partii su cierne figurky blizsie pri hracovi
     * @return
     */
    public boolean isBlackCloser() {
        return blackCloser;
    }

    /**
     * Vrati hodnotu, ci je nastavena promotion
     * @return
     */
    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    void setBlackCloser(boolean blackCloser) {
        this.blackCloser = blackCloser;
    }


    /**
     * Vytvori kopiu aktualneho stavu partie
     * @return vrati kopiu partie
     */
    GameState makeCopyFromActualGame() {
        GameState copyGame = new GameState();
        for (Piece piece: state) {
            try {
                copyGame.state.add((Piece) piece.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        copyGame.setPromotion(this.promotion);
        copyGame.setBlackCloser(this.isBlackCloser());
        return copyGame;
    }

    /**
     * Vrati vsetky mozne pohyby figurky na x, y suradniciach. Zaroven zisti ci je mozna rosada a tiez, ze ak dany
     * pohyb sposobi sach kralovi v rovnakej farbe ako pohybovana figurka, tak tento tah odstrani.
     * @return Zoznam moznych tahov
     */
    ArrayList<Coordinates> getLegalMoves(Piece piece) {
        ArrayList<Coordinates> legalMoves = piece.getLegalMoves(this);

        // rosada
        if (piece instanceof King && !((King) piece).isMoved()) {
            int x = piece.getCoors().getX();
            int y = piece.getCoors().getY();
            int minus = isBlackCloser() ? -1 : 1;
            if (isChecked(this, piece.getBlack()) == null &&
                    getPieceOnPlace(x+1*minus,y) == null &&
                    getPieceOnPlace(x+2*minus,y) == null &&
                    getPieceOnPlace(x+3*minus,y) instanceof Rook &&
                    !((Rook) getPieceOnPlace(x+3*minus,y)).isMoved() &&
                    getPieceOnPlace(x+3*minus, y).getBlack() == piece.getBlack() &&
                    !isSquareAttacked(this, x + 1*minus, y, !piece.getBlack()) &&
                    !isSquareAttacked(this, x + 2*minus, y, !piece.getBlack()))
                legalMoves.add(new Coordinates(x+2*minus, y));

            if (isChecked(this, piece.getBlack()) == null &&
                    getPieceOnPlace(x-1*minus,y) == null &&
                    getPieceOnPlace(x-2*minus,y) == null &&
                    getPieceOnPlace(x-3*minus,y) == null &&
                    getPieceOnPlace(x-4*minus,y) instanceof Rook &&
                    !((Rook) getPieceOnPlace(x-4*minus,y)).isMoved() &&
                    getPieceOnPlace(x-4*minus, y).getBlack() == piece.getBlack() &&
                    !isSquareAttacked(this, x - 1*minus, y, !piece.getBlack()) &&
                    !isSquareAttacked(this, x - 2*minus, y, !piece.getBlack()) &&
                    !isSquareAttacked(this, x - 3*minus, y, !piece.getBlack()))
                legalMoves.add(new Coordinates(x-2*minus, y));
        }

        ArrayList<Coordinates> newlegalMoves =  new ArrayList<>();
        if (legalMoves.size() == 0)
            return legalMoves;
        else {

            for (Coordinates coor :
                    legalMoves) {
                GameState temp = makeCopyFromActualGame();
                makeMove(temp, piece.getCoors().getX(),  piece.getCoors().getY(), coor.getX(), coor.getY());
                Piece checking = isChecked(temp, getPieceOnPlace( piece.getCoors().getX(),piece.getCoors().getY()).getBlack());
                if (checking == null)
                    newlegalMoves.add(coor);
            }
        }
        return newlegalMoves;
    }

    /**
     * Posunie figurku v ramci poskytnutej partie, ak sa jedna o rosadu, posunie navyse aj vezu.
     * Nakoniec nastavi vsetkym pinclom, ze enpassant uz neplati
     * @param state
     * @param startX
     * @param startY
     * @param finishX
     * @param finishY
     */
    void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
        state.getPieceOnPlace(startX, startY).makeMove(state, finishX, finishY);

        if (state.getPieceOnPlace(finishX, finishY) instanceof King) {
            //kral sa pohol
            ((King) state.getPieceOnPlace(finishX, finishY)).setMoved(true);
            int minus = isBlackCloser() ? -1 : 1;
            // rosada velka
            if (startX-finishX==-2*minus) {
                state.getPieceOnPlace(startX+3*minus, startY).makeMove(state, finishX-1*minus, finishY);
            }
            //rosada mala
            if (startX-finishX==2*minus) {
                state.getPieceOnPlace(startX-4*minus, startY).makeMove(state, finishX+1*minus, finishY);
            }
        }
        if (state.getPieceOnPlace(finishX, finishY) instanceof Rook) {
            //pohla sa veza => kvoli rosade
            ((Rook) state.getPieceOnPlace(finishX, finishY)).setMoved(true);
        }
        // po jednom tahu resetni vsetky enpasanty
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) instanceof Pawn && (state.getPieceOnPlace(i, j) != state.getPieceOnPlace(finishX, finishY)))
                    ((Pawn) state.getPieceOnPlace(i, j)).setEnPasant(false);
            }
        }
    }


    /**
     * Vrati figurku na danom mieste
     * @param x
     * @param y
     * @return
     */
    public Piece getPieceOnPlace(int x, int y) {
        for (Piece piece : state) {
            if (piece.getCoors().getY() == y && piece.getCoors().getX() == x)
                return piece;
        }
        return null;
    }

    public List<Piece> getState() {
        return state;
    }

    /**
     * Vytvori partiu, kde su biele figurky pri hracovi
     */
    void setNewStateStandardWhiteFiguresCloser() {
        //white
        for (byte i = 0; i < 8; i++)
            state.add(new Pawn(false, new Coordinates(i, 6)));
        state.add(new Rook(false, new Coordinates(0, 7)));
        state.add(new Rook(false, new Coordinates(7, 7)));
        state.add(new Knight(false, new Coordinates(1, 7)));
        state.add(new Knight(false, new Coordinates(6, 7)));
        state.add(new Bishop(false, new Coordinates(2, 7)));
        state.add(new Bishop(false, new Coordinates(5, 7)));
        state.add(new Queen( false, new Coordinates(3, 7)));
        state.add(new King(false, new Coordinates(4, 7)));

        //black
        for (byte i = 0; i < 8; i++)
            state.add(new Pawn(true, new Coordinates(i, 1)));
        state.add(new Rook(true, new Coordinates(0, 0)));
        state.add(new Rook(true, new Coordinates(7, 0)));
        state.add(new Knight(true, new Coordinates(1, 0)));
        state.add(new Knight(true, new Coordinates(6, 0)));
        state.add(new Bishop(true, new Coordinates(2, 0)));
        state.add(new Bishop(true, new Coordinates(5, 0)));
        state.add(new Queen( true, new Coordinates(3, 0)));
        state.add(new King(true, new Coordinates(4, 0)));
    }


    /**
     * Vytvori partiu, kde su cierne figurky pri hracovi
     */
    void setNewStateStandardBlackFiguresCloser() {
        //white
        for (byte i = 0; i < 8; i++)
            state.add(new Pawn(true, new Coordinates(i, 6)));
        state.add(new Rook(true, new Coordinates(0, 7)));
        state.add(new Rook(true, new Coordinates(7, 7)));
        state.add(new Knight(true, new Coordinates(1, 7)));
        state.add(new Knight(true, new Coordinates(6, 7)));
        state.add(new Bishop(true, new Coordinates(2, 7)));
        state.add(new Bishop(true, new Coordinates(5, 7)));
        state.add(new Queen( true, new Coordinates(4, 7)));
        state.add(new King(true, new Coordinates(3, 7)));

        //black
        for (byte i = 0; i < 8; i++)
            state.add(new Pawn(false, new Coordinates(i, 1)));
        state.add(new Rook(false, new Coordinates(0, 0)));
        state.add(new Rook(false, new Coordinates(7, 0)));
        state.add(new Knight(false, new Coordinates(1, 0)));
        state.add(new Knight(false, new Coordinates(6, 0)));
        state.add(new Bishop(false, new Coordinates(2, 0)));
        state.add(new Bishop(false, new Coordinates(5, 0)));
        state.add(new Queen( false, new Coordinates(4, 0)));
        state.add(new King(false, new Coordinates(3, 0)));
    }

    /**
     * Zisti miesto, kde sa nachadza dana figurka
     * @param lookedPiece
     * @return
     */
    public Coordinates whereIsThis(Piece lookedPiece) {
        for (Piece piece : state) {
            if (piece == lookedPiece)
                return piece.getCoors();
        }
        return null;
    }

    /**
     * Zisti a vrati krala, ktory ma danu farbu a je v sachu
     * @param state
     * @param black
     * @return
     */
    public Piece isChecked(GameState state, boolean black) {
        Piece king = null;
        for (Piece piece : state.getState()) {
            if (piece instanceof King && piece.getBlack() == black) {
                king = piece;
                break;
            }
        }
        for (Piece piece : state.getState()) {
            if (piece.getBlack() != black) {
                ArrayList<Coordinates> coors = piece.getLegalMoves(state);
                for (Coordinates coor: coors) {
                    if (coor.equals(king.getCoors()))
                        return king;
                }
            }
        }
        return null;
    }

    /**
     * Zisti a vrati krala, ktory ma danu farbu a ma sach-mat
     * @param state
     * @param black
     * @return
     */
    public Piece isCheckMated(GameState state, boolean black) {
        Piece king;
        if ((king = isChecked(state, black)) != null) {
            for (Piece piece : state.getState()) {
                if (piece instanceof King && piece.getBlack() == black)
                    king = piece;
            }

            if (state.getLegalMoves(king).size() == 0) {
                if (!isAnyThereLegalMove(state, black))
                    return king;
            }
        }
        return null;
    }

    /**
     * Zisti, ci v danej partii, existuju nejake tahy pre danu farbu
     * @param state
     * @param blackOnMove
     * @return
     */
    boolean isAnyThereLegalMove(GameState state, boolean blackOnMove) {
        for (Piece piece : state.getState()) {
            if (piece.getBlack() == blackOnMove) {
                if (state.getLegalMoves(piece).size() != 0)
                    return true;
            }
        }
        return false;
    }

    /**
     * Zisti, ci nejaka figurka utoci na dane policko. Vyuziva sa pri rosade a pohybe krala.
     * @param state
     * @param x
     * @param y
     * @param byBlack
     * @return
     */
    boolean isSquareAttacked(GameState state, int x, int y, boolean byBlack) {
        for (Piece piece : state.getState()) {
            if (piece.getBlack() == byBlack) {
                for (Coordinates coor : piece.getLegalMoves(state)) {
                    if (coor.equals(new Coordinates(x, y)))
                        return true;
                }
            }
        }
        return false;
    }

}

