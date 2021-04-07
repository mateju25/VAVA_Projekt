package project.model.gameChess.pieces;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.model.gameChess.Coordinates;
import project.model.gameChess.GameState;
import project.model.gameChess.PromotionController;

import java.io.IOException;
import java.util.ArrayList;

public class Pawn extends Piece{
    private boolean isMoved = false;
    private boolean enPasant = false;
    public Pawn(Boolean black)  {
        super(black);
        if (black)
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/BlackPawn.png"));
        else
            pic = new Image(getClass().getResourceAsStream("/project/gui/resources/pictures/WhitePawn.png"));
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }

    public void setEnPasant(boolean enPasant) {
        this.enPasant = enPasant;
    }


    public void makeMove(GameState state, int startX, int startY, int finishX, int finishY) {
        state.getState()[finishX][finishY] = state.getPieceOnPlace(startX, startY);
        state.getState()[startX][startY] = null;

        int minus = 1;
        if (state.isBlackCloser())
            minus = -1;

        if (black) {
            if (state.getPieceOnPlace(finishX, finishY-1*minus) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY-1*minus)).enPasant && state.getPieceOnPlace(finishX, finishY-1*minus).getBlack() != black)
                state.getState()[finishX][finishY -1*minus] = null;
        } else {
            if (state.getPieceOnPlace(finishX, finishY+1) instanceof Pawn && ((Pawn) state.getPieceOnPlace(finishX, finishY+1*minus)).enPasant && state.getPieceOnPlace(finishX, finishY+1*minus).getBlack() != black)
                state.getState()[finishX][finishY +1*minus] = null;
        }

        isMoved = true;

        if (Math.abs(finishY - startY) == 2)
            enPasant = true;

        if (finishY == 0 || finishY == 7) {
            if (state.isPromotion() == null)
                return;
            if (state.isPromotion().equals("-1")) {
                Parent root = null;
                FXMLLoader loader = null;
                try {
                    loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/project/model/gameChess/promotionDialog.fxml"));
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(root);

                PromotionController controller = loader.getController();
                controller.setBlack(this.black);

                Stage window = new Stage();
                window.setResizable(false);
                window.setAlwaysOnTop(true);
                window.initModality(Modality.APPLICATION_MODAL);
                window.setScene(scene);
                window.showAndWait();
                state.getState()[finishX][finishY] = controller.getChosedPiece() == null ? new Queen(this.black) : controller.getChosedPiece();
            } else {
                switch (state.isPromotion()) {
                    case "q": state.getState()[finishX][finishY] = new Queen(this.black); break;
                    case "k": state.getState()[finishX][finishY] = new Knight(this.black); break;
                    case "b": state.getState()[finishX][finishY] = new Bishop(this.black); break;
                    case "r": state.getState()[finishX][finishY] = new Rook(this.black); break;
                }
            }
        }

    }

    @Override
    public ArrayList<Coordinates> getLegalMoves(GameState state, int x, int y) {
        ArrayList<Coordinates> result = new ArrayList<>();
        if (y == 0 || y == 7)
            return result;
        int minus = 1;
        if (state.isBlackCloser())
            minus = -1;
        if (black) {
            if (state.getPieceOnPlace(x, y+1*minus) == null) {
                result.add(new Coordinates(x, y+1*minus));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y+2*minus) == null)
                        result.add(new Coordinates(x, y+2*minus));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y+1*minus) != null && state.getPieceOnPlace(x-1, y+1*minus).getBlack() != black))
                result.add(new Coordinates(x-1, y+1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y+1*minus) != null && state.getPieceOnPlace(x+1, y+1*minus).getBlack() != black))
                result.add(new Coordinates(x+1, y+1*minus));

            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
                result.add(new Coordinates(x-1, y+1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
                result.add(new Coordinates(x+1, y+1*minus));
        } else {
            if (state.getPieceOnPlace(x, y-1*minus) == null) {
                result.add(new Coordinates(x, y-1*minus));
                if (!isMoved)
                    if (state.getPieceOnPlace(x, y-2*minus) == null)
                        result.add(new Coordinates(x, y-2*minus));
            }
            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y-1*minus) != null && state.getPieceOnPlace(x-1, y-1*minus).getBlack() != black))
                result.add(new Coordinates(x-1, y-1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y-1*minus) != null && state.getPieceOnPlace(x+1, y-1*minus).getBlack() != black))
                result.add(new Coordinates(x+1, y-1*minus));

            if (x-1 >=0 && (state.getPieceOnPlace(x-1, y) instanceof Pawn && state.getPieceOnPlace(x-1, y).getBlack() != black && ((Pawn) state.getPieceOnPlace(x - 1, y)).enPasant))
                result.add(new Coordinates(x-1, y-1*minus));
            if (x+1 <=7 && (state.getPieceOnPlace(x+1, y) instanceof Pawn && state.getPieceOnPlace(x+1, y).getBlack() != black  && ((Pawn) state.getPieceOnPlace(x + 1, y)).enPasant))
                result.add(new Coordinates(x+1, y-1*minus));
        }

        return result;
    }
}
