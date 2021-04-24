package project.model.gameChess;

/**
 * @author Matej Delincak
 *
 * Zoznam dostupnych signalov pre komunikaciu medzi triedou Chessboard a GameBoardController
 */
public enum Signalization {
    // sach mat
    CHECKMATE,
    // pat
    STALEMATE,
    // sach
    CHECK,
    // normalny pohyb
    NORMAL,
    // vypytany tah na nespravnu figurku
    NO_PIECE,
    // dosiel cas
    NO_TIME,
    // vzdal som sa
    GIVE_UP_ME,
    // vzdal sa druhy
    GIVE_UP_OTHER
}
