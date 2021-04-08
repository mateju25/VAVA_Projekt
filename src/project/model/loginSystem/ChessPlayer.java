package project.model.loginSystem;

public class ChessPlayer {
    private String name;
    private boolean administrator = false;

    public ChessPlayer(String name, boolean administrator) {
        this.name = name;
        this.administrator = administrator;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public String getName() {
        return name;
    }
}
