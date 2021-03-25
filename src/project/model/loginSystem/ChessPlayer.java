package project.model.loginSystem;

public class ChessPlayer {
    private String name;
    private String password;
    private String email;

    public ChessPlayer(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
