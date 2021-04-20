package project.model.databaseSystem;

public class ChessPlayer {
    private String name;
    private String password;
    private String email;
    private boolean administrator = false;

    public ChessPlayer(String name, String password, String email, boolean administrator) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.administrator = administrator;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
}
