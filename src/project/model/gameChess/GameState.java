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
    private List<List<Piece>> matrix = new ArrayList<>();
    private Piece[][] state = new Piece[8][8];
    private boolean blackCloser = false;
    private String promotion = null;

    GameState() {
//        for (int i = 0; i < 8; i++) {
//            matrix.
//        }
        for(Piece[] array : state) Arrays.fill(array, null);
    }

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
        copyGame.setPromotion(this.promotion);
        copyGame.setBlackCloser(this.isBlackCloser());
        return copyGame;
    }

    /**
     * Vrati vsetky mozne pohyby figurky na x, y suradniciach. Zaroven zisti ci je mozna rosada a tiez, ze ak dany
     * pohyb sposobi sach kralovi v rovnakej farbe ako pohybovana figurka, tak tento tah odstrani.
     * @param x
     * @param y
     * @return Zoznam moznych tahov
     */
    ArrayList<Coordinates> getLegalMoves(int x, int y) {
        ArrayList<Coordinates> legalMoves =  new ArrayList<>();
        if (getPieceOnPlace(x,y) == null)
            return legalMoves;
        else
            legalMoves = getPieceOnPlace(x,y).getLegalMoves(this, x, y);

        // rosada
        if (getPieceOnPlace(x,y) instanceof King && !((King) getPieceOnPlace(x,y)).isMoved()) {
            int minus = isBlackCloser() ? -1 : 1;
            if (isChecked(this, getPieceOnPlace(x,y).getBlack()) == null &&
                    getPieceOnPlace(x+1*minus,y) == null &&
                    getPieceOnPlace(x+2*minus,y) == null &&
                    getPieceOnPlace(x+3*minus,y) instanceof Rook &&
                    !((Rook) getPieceOnPlace(x+3*minus,y)).isMoved() &&
                    getPieceOnPlace(x+3*minus, y).getBlack() == getPieceOnPlace(x,y).getBlack() &&
                    !isSquareAttacked(this, x + 1*minus, y, !getPieceOnPlace(x, y).getBlack()) &&
                    !isSquareAttacked(this, x + 2*minus, y, !getPieceOnPlace(x, y).getBlack()))
                legalMoves.add(new Coordinates(x+2*minus, y));
            if (isChecked(this, getPieceOnPlace(x,y).getBlack()) == null &&
                    getPieceOnPlace(x-1*minus,y) == null &&
                    getPieceOnPlace(x-2*minus,y) == null &&
                    getPieceOnPlace(x-3*minus,y) == null &&
                    getPieceOnPlace(x-4*minus,y) instanceof Rook &&
                    !((Rook) getPieceOnPlace(x-4*minus,y)).isMoved() &&
                    getPieceOnPlace(x-4*minus, y).getBlack() == getPieceOnPlace(x,y).getBlack() &&
                    !isSquareAttacked(this, x - 1*minus, y, !getPieceOnPlace(x, y).getBlack()) &&
                    !isSquareAttacked(this, x - 2*minus, y, !getPieceOnPlace(x, y).getBlack()) &&
                    !isSquareAttacked(this, x - 3*minus, y, !getPieceOnPlace(x, y).getBlack()))
                legalMoves.add(new Coordinates(x-2*minus, y));
        }

//        if ((isChecked(this, getPieceOnPlace(x,y).getBlack()) != null) && (isChecked(this, getPieceOnPlace(x,y).getBlack()).getBlack() != getPieceOnPlace(x,y).getBlack()))
//            return legalMoves;
        ArrayList<Coordinates> newlegalMoves =  new ArrayList<>();
        if (legalMoves.size() == 0)
            return legalMoves;
        else {

            for (Coordinates coor :
                    legalMoves) {
                GameState temp = makeCopyFromActualGame();
                makeMove(temp, x, y, coor.getX(), coor.getY());
                Piece checking = isChecked(temp, getPieceOnPlace(x,y).getBlack());
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
        state.getPieceOnPlace(startX, startY).makeMove(state, startX, startY, finishX, finishY);

        if (state.getPieceOnPlace(finishX, finishY) instanceof King) {
            //kral sa pohol
            ((King) state.getPieceOnPlace(finishX, finishY)).setMoved(true);
            int minus = isBlackCloser() ? -1 : 1;
            // rosada velka
            if (startX-finishX==-2*minus) {
                state.getPieceOnPlace(startX+3*minus, startY).makeMove(state, startX+3*minus, startY, finishX-1*minus, finishY);
            }
            //rosada mala
            if (startX-finishX==2*minus) {
                state.getPieceOnPlace(startX-4*minus, startY).makeMove(state, startX-4*minus, startY, finishX+1*minus, finishY);
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
        return state[x][y];
    }

    public Piece[][] getState() {
        return state;
    }

    /**
     * Vytvori partiu, kde su biele figurky pri hracovi
     */
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


    /**
     * Vytvori partiu, kde su cierne figurky pri hracovi
     */
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
        state[4][7] = new Queen( true);
        state[3][7] = new King(true);

        //black
        for (byte i = 0; i < 8; i++)
            state[i][1] = new Pawn(false);
        state[0][0] = new Rook( false);
        state[7][0] = new Rook(false);
        state[1][0] = new Knight(false);
        state[6][0] = new Knight(false);
        state[2][0] = new Bishop(false);
        state[5][0] = new Bishop( false);
        state[4][0] = new Queen(false);
        state[3][0] = new King( false);
    }

    /**
     * Zisti miesto, kde sa nachadza dana figurka
     * @param piece
     * @return
     */
    public Coordinates whereIsThis(Piece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.getPieceOnPlace(i, j) == piece)
                    return new Coordinates(i, j);
            }
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
        Coordinates king = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) instanceof King && state.getPieceOnPlace(i, j).getBlack() == black)
                    king = new Coordinates(i, j);
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) != null && state.getPieceOnPlace(i, j).getBlack() != black) {
                    ArrayList<Coordinates> coors = state.getPieceOnPlace(i, j).getLegalMoves(state, i, j);
                    for (Coordinates coor:
                            coors) {
                        if (coor.getY() == king.getY() && coor.getX() == king.getX())
                            return state.getPieceOnPlace(king.getX(), king.getY());
                    }
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
        if (isChecked(state, black) != null) {
            Coordinates king = null;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (state.getPieceOnPlace(i, j) instanceof King && state.getPieceOnPlace(i, j).getBlack() == black)
                        king = new Coordinates(i, j);
                }
            }
            if (state.getLegalMoves(king.getX(), king.getY()).size() == 0) {
                if (!isAnyThereLegalMove(state, black))
                    return state.getPieceOnPlace(king.getX(), king.getY());
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

    /**
     * Zisti, ci nejaka figurka utoci na dane policko. Vyuziva sa pri rosade a pohybe krala.
     * @param state
     * @param x
     * @param y
     * @param byBlack
     * @return
     */
    boolean isSquareAttacked(GameState state, int x, int y, boolean byBlack) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state.getPieceOnPlace(i, j) != null && state.getPieceOnPlace(i, j).getBlack() == byBlack) {
                    for (Coordinates coor : state.getPieceOnPlace(i, j).getLegalMoves(state, i, j)) {
                        if (coor.getX() == x && coor.getY() == y)
                            return true;
                    }
                }

            }
        }
        return false;
    }

}

