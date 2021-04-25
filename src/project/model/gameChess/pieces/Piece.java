package project.model.gameChess.pieces;

import javafx.scene.image.Image;
import project.model.databaseSystem.LoginConnection;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;

import java.util.ArrayList;

/**
 * @author Matej Delincak
 *
 * Hlavna trieda figurky. Obsahuje informacie o farbe, obrazku.
 */
public abstract class Piece implements Cloneable{
    protected Boolean black;
    protected Image pic;
    protected Coordinates coors;


    public static int SetNumber = LoginConnection.getInstance().getFavouritePieces();

    public Piece(Boolean black, Coordinates coors) {
        this.black = black;
        this.coors = coors;
    }

    /**
     * Vykona pohyb figurky.
     * @param state
     * @param finishX
     * @param finishY
     */
    public void makeMove(GameState state, int finishX, int finishY) {
        Piece startP = state.getPieceOnPlace(coors.getX(), coors.getY());
        Piece finalP = state.getPieceOnPlace(finishX, finishY);
        if (finalP != null) {
            startP.setCoors(finalP.getCoors());
            state.getState().remove(finalP);
        } else {
            startP.setCoors(new Coordinates(finishX, finishY));
        }
    }

    public Coordinates getCoors() {
        return coors;
    }

    public void setCoors(Coordinates coors) {
        this.coors = coors;
    }

    public abstract ArrayList<Coordinates> getLegalMoves(GameState state);

    public Image getPic() {
        return this.pic;
    }

    public Boolean getBlack() {
        return this.black;
    }

    public void setPic(Image pic) {
        this.pic = pic;
    }

    /**
     * Vlozi mozny pohyb figurky do zoznamu, ak je takyto pohyb mozny
     * @param list
     * @param state
     * @param x
     * @param y
     * @return
     */
    protected boolean insertMove(ArrayList<Coordinates> list, GameState state, int x, int y) {
        if (state.getPieceOnPlace(x, y) == null)
            list.add(new Coordinates(x, y));
        else {
            if (state.getPieceOnPlace(x, y).getBlack() != this.getBlack())
                list.add(new Coordinates(x, y));
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

}
